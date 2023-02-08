SizeModuleMappingInfo = provider(
    fields = {
        'jars' : 'jars',
        'assets': 'assets',
        'resources': 'resources'
    }
)


def _describe(name, obj, exclude):
    """Print the properties of the given struct obj
    
    Args:
      name: the name of the struct we are introspecting.
      obj: the struct to introspect
      exclude: a list of names *not* to print (function names)
    """
    for k in dir(obj):
        if hasattr(obj, k) and k not in exclude:
            v = getattr(obj, k)
            t = type(v)
            print("%s.%s<%r> = %s" % (name, k, t, v))


def _ruler_aspect_impl(target, ctx):
    print("\n\n\n-------------------------------------")
    print(target.label.name)
    print(ctx.rule.kind)

    if hasattr(ctx.rule.attr, 'deps'):
        print(ctx.rule.attr.deps)

    # _describe("ctx", target, exclude = ["output_group"])

    # if JavaInfo in target:
    #     # print(target[JavaInfo])
    #     _describe("ctx",target[JavaInfo], exclude = ["output_group"])

    # This gets all of the output jars in the target
    # We only care about the main output jar with the format: libname.jar
    jars = [{"jar": jar.path, "module": target.label.name} for jar in target[DefaultInfo].files.to_list() if jar.extension == "jar"]
    

    # Get the android assets if present
    assets = []
    if hasattr(ctx.rule.attr, 'assets'):
        for asset in ctx.rule.attr.assets:
            for f in asset.files.to_list():
                assets.append({"filename": f.basename, "module": target.label.name})

    resources = []
    if hasattr(ctx.rule.attr, 'resource_files'):
        for resource in ctx.rule.attr.resource_files:
            for f in resource.files.to_list():
                resources.append({"filename": f.basename, "module": target.label.name})


    # Add things from dependencies to build things up recursively
    if hasattr(ctx.rule.attr, 'deps'):
        for dep in ctx.rule.attr.deps:
            jars.extend(dep[SizeModuleMappingInfo].jars)
            assets.extend(dep[SizeModuleMappingInfo].assets)
            resources.extend(dep[SizeModuleMappingInfo].resources)

    return [SizeModuleMappingInfo(jars = jars, assets = assets, resources=resources)]

ruler_aspect = aspect(
    implementation = _ruler_aspect_impl,
    attr_aspects = ['deps'],
)

def _ruler_rule_impl(ctx):
    inputs = []
    for dep in ctx.attr.deps:
        print(dep[SizeModuleMappingInfo].to_json())
        # files = dep[SizeModuleMappingInfo].jars
        # for f in files:
        #     inputs.append(f[0])
    print(inputs)

    output_file = ctx.actions.declare_file(ctx.label.name + ".output")
    print(output_file)
    command = "touch %s" % (output_file.path)

    # Depend on the outputs from our direct dependencies to cause them to build
    deps = []

    # for dep in ctx.attr.deps:
    #     deps = deps + dep[DefaultInfo].files
    deps = depset([], transitive = [dep[DefaultInfo].files for dep in ctx.attr.deps])

    print(deps)

    ctx.actions.run_shell(
        inputs = deps,
        outputs = [output_file],
        command = command
    )

    return [DefaultInfo(files = depset([output_file]))]

ruler_rule = rule(
    implementation = _ruler_rule_impl,
    attrs = {
        'deps' : attr.label_list(aspects = [ruler_aspect]),
    },
)
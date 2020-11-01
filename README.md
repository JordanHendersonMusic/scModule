# scModule
Module system for Supercollider using prototypes.

## Instructions
Clone repo and see module_example_main.sc for how the top file should be laid out.

Each module is intended to be self contained and connected together in the main file.
This makes simple tasks verbose, but more complex projects can be broken into self contained units and patched together at compile time.

To load a module the main file must first manually load the module.sc.

```supercollider
//main.sc
(File.getcwd +/+ "module.sc").load;
```

Then call mk on the 'global' module object with the relative path.

```supercollider
//main.sc
~my_fanastic_mod = ~module.mk.("modules/my_fantastic_module.sc");
```
Almost everything is user definable after this, however each module file has a few 'global' variable that are special. 

#### An fictional example of a main file
Here 12 channel noise is created.
A reverb synth listens, mixes it down to 4 chans, applies an effect, mixes it back up to 8.
Both are routed to the output that mixes both up to 36 channels.
```supercollider
//main.sc

~noise = ~module.mk.("modules/noise.sc");
~noise.opt.numOuts = 12;
~noise.use { ~init.() };

~rev = ~module.mk.("modules/reverb.sc");
~rev.opt.internalNumChans = 4;
~rev.opt.numOuts = 8;
~rev.opt.target = ~noise.synth; 
~rev.opt.addAction = \addAfter;
~rev.use { ~init.() };

~output = ~module.mk.("modules/output.sc");
~output.opt.speakerNum = 36;
~output.opt.inBuses = [~rev.outBus, ~noise.outBus];
~output.use { ~init.() };
```

## ~m
```supercollider
// constructed by default
~m = ();
```
An IdentityDictionary of methods related to the module system.
```supercollider
// in modules/my_mod.sc
~m.find_DANGER.( \variable_name )
```
A function that looks for a variable from the top environment down, if there are multiple objects under the same variable the it returns the highest one.
```supercollider
// in modules/my_mod.sc
~m.mk_unq.(\name)
```
A function that makes a unique name by concatenating the module name and unique id number to the beginning of the input. 
Use this to create SynthDef names for example.

## ~submodules
```supercollider
// in modules/my_mod.sc -- usually at the top
~submodules = ["array_of_relative_paths_to_submodules"];
```
These are added as parents to the environment. 
Meaning any 'global' variables can be accessed like usual
```supercollider
// in modules/sub_module.sc
~foo = \bar;
```
```supercollider
// in modules/my_mod.s
~submodules = ["modules/sub_module.sc"];
~foo // returns \bar
```

## ~opt
```supercollider
// in modules/my_mod.sc
~opt = (\my_settings: nil);
```
An event (key value pairs) of options that are to be set in the main file, along with any defaults.
In the top file, this would look this this:
```supercollider
// in main.sc
~my_module.opt.my_setting = \my_value;
```

## ~nodes
```supercollider
// constructed by default
~nodes = List[]
```
A list of all server nodes.
When ever you make something you might want to free when the module is destroyed, add it here.
Typically, synths, buses, groups.
Do not use ~nodes to stored objects in, you can not get them out. Instead make 'global' variables.
```supercollider
// in modules/my_mod.sc
~outBus = Bus.(s, ~opt.numChans);
~nodes.add(~outBus);
~nodes.add(Synth(name));
```
## ~freeAll.()
This goes through the ~nodes list and tries to free each node. Then it deletes the nodes.
It is constructed by default.

## ~public
A list of strings indicating external variables. In is constructed by default.
Add symbols so that the users (in the top file) can quickly see what variables they are allowed to use.
```supercollider
// in modules/my_mod.sc
~outBus = Bus.(s, ~opt.numChans);
~public.add(\outBus);
```
```supercollider
// in main.sc
~my_mod.public // posts a list of variables the use has access to.
```
# Recommendation for init
```supercollider
// in modules/my_mod.sc
~opt = (\numChans, nil);
~init = {
  Routine.run({
    // define synthdefs and construct busses here.
    // making sure to add everything to ~nodes.
  })
};
```
```supercollider
// in main.sc
~my_mod.opt.numChans = 2;
~my_mod.use { ~init.() };     // defines everything then starts synthdef.
~my_mod.use { ~freeAll() };   // removes all nodes from server, buses, groups and buffers... whatever is in ~my_mod.nodes
```

# modules/audio_multichan_reduce.sc
In the examples provided the plan has been for each module to have one output bus, but accept an array of input buses.
To this the file 'modules/audio_multichan_reduce.sc' is used as a submodule.
It contains a panning and a mixing function that allows the mixing of an array of buses of any size to a target size.
The size of the buses must be known as this takes place at compile time.

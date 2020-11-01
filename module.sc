/// Functionality relating to the module system
// this is functionality relating to the module system
~module_base_core = IdentityDictionary[
		\find_DANGER -> {{ |f| topEnvironment.use({ f.(); }) }}, 		// returns varible closes to top - defined
		\mk_unq -> {{|n| (~id.asString ++ "_" ++ n.asString).asSymbol}},
];
~module_base_core.know = true;

/// Functionality used by the module itself
~module_base = IdentityDictionary[
	\m -> ~module_base_core,
	\opt -> (), 					// options 
	\nodes -> List[], 				// server nodes
	\public -> List[],				// symbol to external stuff
];
~module_base.add(\freeAll -> {
	~nodes.do({ |n| 
		try {n.freeAll} {};
		try {n.free}	{};
	});
	~nodes.clear();
});
~module_base.know = true;

/// The ~module, exists in topEnvironment. Used to load modules, set parents, and give them ids.
~module = ();
~module.add(\unique_number -> 0);
~module.add(\get_id -> {
	~module[\unique_number] = ~module[\unique_number] + 1;
	~module[\unique_number];
});

~module.add(\mk -> {{
	|path, isSub=false, indent=""|
	var rootPath = topEnvironment.use({File.getcwd;});
	var completePath = rootPath +/+ path;
	var file = File.open(completePath, "r");
	var string = file.readAllString;
	var tempEnv = Environment();
	var id_root = path.split($/);
	var subs;
	tempEnv.know = true;
	isSub.not.if( {
		tempEnv.id = id_root[id_root.size - 1].split($.)[0].asString ++ "_" ++ ~module[\get_id].().asString;
	});
	isSub.if({},{" ".postln;});
	isSub.not.if({(indent + "Loading module" + path).postln}, {(indent + "Loading submodule" + path).postln});
	tempEnv.insertParent(~module_base.deepCopy(), inf);
	tempEnv.use({ string.compile.() });
	tempEnv.submodules.isNil.not.if({(indent + "\t" + "Prepping submodules" + tempEnv.submodules).postln},{});
	subs = tempEnv.submodules.collect({ |p| ~module.mk.(p, true, indent + "\t\t"); });
	subs.do({ |sub| tempEnv.insertParent(sub.deepCopy(), 0); });
	isSub.if({},{" ".postln;});
	tempEnv;
}});

/// --- Output Module ---
~submodules = ["modules/audio_multichan_reduce.sc", "modules/audio_dynamic.sc"];

/// External

~opt = (
	\speakerNum: \num_of_physical_speakers,
	\inBuses: \array_of_buses,
	\addAction: \addToTail,
	\comp: (\th: -10, \ratio: 1/2, \atk: 0.01, \rel: 0.1),
	\target: s
);

/// Private

/// Init
~init = {
	"Initalising the Output Module".postln;
	//
	~freeAll.();
	//
	~opt.inBuses = ~opt.inBuses.isArray.if( ~opt.inBuses, [~opt.inBuses] );
	Routine.run({
		var name = ~m.mk_unq.(\output);
		//
		("Defining the output SynthDef with name: " ++ name.asString).postln;
		//
		SynthDef(name, {
			var sig;
			sig = ~mix_input_buses.(~opt.speakerNum, ~opt.inBuses);
			sig = ~comp.(sig, ~opt.comp.th.dbamp, ~opt.comp.ratio, ~opt.comp.atk, ~opt.comp.rel);
			Out.ar(0, sig.tanh);
		}).add;
		//
		s.sync;
		//
		"Making the Output Synth".postln;
		~nodes.add(Synth(name, target: ~opt.target, addAction: ~opt.addAction));
		"Done -- Output Module intalisied".postln;
		"".postln;
	});
};

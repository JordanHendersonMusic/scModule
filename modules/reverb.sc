/// --- Reverb  Module ---
~submodules = ["modules/audio_multichan_reduce.sc"];

/// External
~opt = (
	\inBuses:  \a_Bus_or_array_of_Buses,
	\outNumChans: 2, // the outNumChans used internally
	\addAction: \addAfter,
	\target: s
);

~outBus = \a_bus; ~public.add(\outBus);

/// Private

/// Init
~init = {
	"Initalising the Reverb Module".postln;
	~freeAll.();
	// forces outBus to be an array.
	~outBus = Bus.audio(s, ~opt.outNumChans);
	~nodes.add(~outBus);
	//
	Routine.run({
		var name = ~m.mk_unq.(\beep);
		//
		("Defining the Reverb SynthDef with name: " ++ name.asString).postln;
		SynthDef(name, {
			var sig, rev;
			sig = ~mix_input_buses.(~opt.outNumChans, ~opt.inBuses);
			rev = CombN.ar( [sig] * -15.dbamp, 1, [0.3,0.8,0.34,0.15,0.53,0.724,0.54,0.726], 4);
			rev = Mix.ar(rev);
			Out.ar(~outBus, ~mix.(~outBus.numChannels, ~opt.outNumChans, rev));
		}).add;
		//
		s.sync;
		"Making the REverb Synth".postln;
		~nodes.add(Synth(name, target: ~opt.target, addAction: ~opt.addAction));
		"Done -- Reverb Module intalisied".postln;
		"".postln;
	});
};




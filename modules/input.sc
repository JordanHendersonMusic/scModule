/// --- Input Module ---
~submodules = ["modules/audio_multichan_reduce.sc"];

/// Public

~opt = (
	\inBusChannels: \array_of_channels_for_SoundIn,
	\outChanNum: nil,
	\addAction: \addToHead,
	\target: s
);

~outBus = \the_output_bus_access_me_externally;
~synth = \ref_to_synth;
~public.add(\outBus);
~public.add(\synth);

/// Private

/// Init
~init = { 
	"Initalising the Input Module".postln;
	~freeAll.();	
	//
	~opt.inBusChannels = ~opt.inBusChannels.isArray.if(~opt.inBusChannels, [~opt.inBusChannels]);
	Routine.run({
		var name = ~m.mk_unq.(\input);
		//
		~outBus = Bus.audio(s, ~opt.outChanNum ? ~opt.inBusChannels.size);
		~nodes.add(~outBus);
		//
		("Defining the input SynthDef with name: " ++ name.asString).postln;
		SynthDef(name, {
			var sig = SoundIn.ar(~opt.inBusChannels);
			// makes output same channum as input if outNumChannels == nil
			sig = ~mix.(~opt.outChanNum ? ~opt.inBusChannels.size, ~opt.inBusChannels.size, sig);
			Out.ar(~outBus, sig);
		}).add;
		//
		s.sync;
		//
		"Making the Input Synth".postln;
		~synth = Synth(name, target: ~opt.target, addAction: ~opt.addAction);
		~nodes.add(~synth);
		"Done -- Input Module intalisied".postln;
		"".postln;
	});
};

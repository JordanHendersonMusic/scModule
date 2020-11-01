/// --- Example Pattern Module ---
~submodules = ["modules/audio_multichan_reduce.sc"];

/// External
~opt = (
	\outNumChans: 2,
	\addAction: \addAfter,
	\target: s
);

~group = \a_group; 	~public.add(\group);
~pat_def = \a_pattern;	~public.add(\pat_def);
~pat = nil; 		~public.add(\pat); // an EventStreamPlayer;
~outBus = \a_bus;	~public.add(\outBus);

/// Private

/// Init
~init = {
	"Initalising the Example Pattern Module".postln;
	~freeAll.();
	"Freed".postln;
	// forces outBus to be an array.
	~outBus = Bus.audio(s, ~opt.outNumChans);
	~nodes.add(~outBus);
	"Made Bus".postln;
	Routine.run({
		var name = ~m.mk_unq.(\beep);
		//
		~group = Group(~opt.target, ~opt.addAction);
		~nodes.add(~group);
		//
		("Defining the Example Pattern SynthDef with name: " ++ name.asString).postln;
		SynthDef(name, {
			|freq=220, atk=0.01, dec=2, panSpd=0.2, amp=1|
			var env = EnvGen.kr(Env.perc(atk, dec), doneAction: 2);
			var sig = SinOsc.ar(freq); 
			sig = sig * env * amp;
			Out.ar(~outBus, ~pan.(~outBus.numChannels, 1, sig, LFNoise2.kr(panSpd)));
		}).add;
		//
		s.sync;
		//
		"Making the Pattern".postln;
		~pat_def = Pbind(
			\instrument, name,
			\group, ~group,
			\dur, Pseq([1, 1, 1, 0.5, 0.5, 1],inf),
			\freq, Pseq([220, 230, 500, 340], inf),
			\atk, Pwhite(0.01, 1),
			\dec, Pwhite(1, 5),
			\db, Pwhite(-20, 0),
			\panSpd, Pseq([0.2,10,0.1],inf),
		);
		"Done -- Output Module intalisied".postln;
		"".postln;
	});
};

~play = { ~pat !? ( _.play) ?? {~pat = ~pat_def.play}};
~stop = { ~pat !? ( _.stop) };

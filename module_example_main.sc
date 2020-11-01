(File.getcwd +/+ "module.sc").load;

s.boot; s.plotTree; s.scope;

/// Input Module
~input = ~module.mk.("modules/input.sc")
~input.opt.inBusChannels = [0];
~input.use { ~init.() };


/// Beep Pattern Module
~beep = ~module.mk.("modules/example_pattern.sc");
~beep.opt.outNumChans = 6;
~beep.opt.target = ~input.synth; ~beep.opt.addAction = \addAfter;
~beep.use { ~init.() };
// beep controls
~beep.use { ~play.() };	
~beep.use { ~stop.() };

/// Reverb Module
~rev = ~module.mk.("modules/reverb.sc");
~rev.opt.inBuses = [~beep.outBus];
~rev.opt.target = ~beep.group;
~rev.opt.outNumChans = 2;
~rev.use { ~init.() };
~rev.use { ~freeAll.() };

/// Output Module
~output = ~module.mk.("modules/output.sc");
~output.opt.speakerNum = 2;
~output.opt.inBuses = ~rev.outBus;
~output.use { ~init.() };
~output.use { ~freeAll.() };



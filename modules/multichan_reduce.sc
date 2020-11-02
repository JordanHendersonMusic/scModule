/// --- Audio Multichannel Reduce Module ---
~getrate = { |sig| (sig.rate == \audio).if(\ar,\kr) };

~pan = {
	|targetCount, currentCount, sig, panCtl|
	var r = ~getrate.(sig);
	case 
	{targetCount == currentCount } {sig}
	{targetCount == 1} {Mix.perform(r, sig)}
	{targetCount == 2} {Pan2.perform(r, sig, panCtl)}
	{targetCount >= 3} {PanAz.perform(r, targetCount, sig, panCtl)}
};

~mix = {
	|targetCount, currentCount, sig |
	var r = ~getrate.(sig);
	case
	{targetCount == currentCount } {sig}
	{targetCount == 1} {Mix.perform(r, sig)}
	{targetCount == 2} {Splay.perform(r, sig)}
	{targetCount >= 3} {SplayAz.perform(r, targetCount, sig, 1)}
};

~mix_input_buses = {
	|targetCount, col|
	var r = ~getrate.(sig);
	Mix.perform(r, col.collect { |b| ~mix.(targetCount, b.numChannels, In.perform(r, b, b.numChannels)) } );
};

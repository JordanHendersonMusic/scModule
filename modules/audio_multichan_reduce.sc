/// --- Audio Multichannel Reduce Module ---

~pan = {
	|targetCount, currentCount, sig, panCtl|
	case 
	{targetCount == currentCount } {sig}
	{targetCount == 1} {Mix.ar(sig)}
	{targetCount == 2} {Pan2.ar(sig, panCtl)}
	{targetCount >= 3} {PanAz.ar(targetCount, sig, panCtl)}
};

~mix = {
	|targetCount, currentCount, sig |
	case
	{targetCount == currentCount } {sig}
	{targetCount == 1} {Mix.ar(sig)}
	{targetCount == 2} {Splay.ar(sig)}
	{targetCount >= 3} {SplayAz.ar(targetCount, sig, 1)}
};

~mix_input_buses = {
	|targetCount, col|
	Mix.ar( col.collect { |b| ~mix.(targetCount, b.numChannels, In.ar(b, b.numChannels)) } );
};

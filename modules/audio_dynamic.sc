/// --- Audio Dynamic Module ---

~comp = {
	|sig, th, ratio, atk, rel|
	Compander.ar(sig, sig, th, 1.0, (ratio > 1).if(ratio.reciprocal, ratio), atk, rel);
};


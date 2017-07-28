package xfamily.Normal;


public class NormalNonConjugateFactorySampled implements NormalNonConjugateFactory{
	@Override public NormalNonConjugateHierarchySampled construct(NormalGammaIndependent prior) {
		return new NormalNonConjugateHierarchySampled(prior);
	}
	@Override public void destruct(NormalGammaIndependent prior, NormalNonConjugateHierarchy hierarchy) {
		
	}
}

package xfamily.Normal;


public class NormalConjugateFactorySampled implements NormalConjugateFactory{
	@Override public NormalConjugateHierarchySampled construct(NormalGamma prior) {
		return new NormalConjugateHierarchySampled(prior);
	}
	@Override public void destruct(NormalGamma prior, NormalConjugateHierarchy hierarchy) {
		
	}
}

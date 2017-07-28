package xfamily.Normal;

public class NormalConjugateFactoryMarginalized implements NormalConjugateFactory{
	@Override public NormalConjugateHierarchy construct(NormalGamma prior) {
		return new NormalConjugateHierarchyMarginalized(prior);
	}
	@Override public void destruct(NormalGamma prior, NormalConjugateHierarchy hierarchy) {
		
	}
}

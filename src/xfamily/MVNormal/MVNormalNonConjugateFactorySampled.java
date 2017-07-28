package xfamily.MVNormal;


public class MVNormalNonConjugateFactorySampled implements MVNormalNonConjugateFactory{
	@Override public MVNormalNonConjugateHierarchySampled construct(MVNormalWishartIndependent prior) {
		return new MVNormalNonConjugateHierarchySampled(prior);
	}
	@Override public void destruct(MVNormalWishartIndependent prior, MVNormalNonConjugateHierarchy hierarchy) {
		
	}
}

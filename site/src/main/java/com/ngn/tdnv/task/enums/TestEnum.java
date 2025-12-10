package com.ngn.tdnv.task.enums;

public enum TestEnum {
	TEST1{
		@Override
		public int doAdd(int a,int b) {
			return a + b;
		}
	};
	
	public abstract int doAdd(int a,int b);
}

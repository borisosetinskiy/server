package com.ob.common.actor;

import java.io.Serializable;
/**
 * Created by boris on 19.04.2016.
 */
public interface Tuples {
	static class Tuple1<T1> implements Serializable{
		private static final long serialVersionUID = 1776545318825288122L;
		public final T1 t1;
		public Tuple1(T1 t1) {
			super();
			this.t1 = t1;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((t1 == null) ? 0 : t1.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Tuple1 other = (Tuple1) obj;
			if (t1 == null) {
				if (other.t1 != null)
					return false;
			} else if (!t1.equals(other.t1))
				return false;
			return true;
		}
		@Override
		public String toString() {
			return "[t1=" + t1 + "]";
		}

	}
	static class Tuple2<T1,T2> extends Tuple1<T1> implements Serializable{
		private static final long serialVersionUID = 3543272933011707249L;
		public final T2 t2;
		public Tuple2(T1 t1, T2 t2) {
			super(t1);
			this.t2 = t2;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((t2 == null) ? 0 : t2.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			boolean result = super.equals(obj);
			if(!result) return result;
			Tuple2 other = (Tuple2) obj;
			if (t2 == null) {
				if (other.t2 != null)
					return false;
			} else if (!t2.equals(other.t2))
				return false;
			return true;
		}
		@Override
		public String toString() {
			return super.toString()+"[t2=" + t2 + "]";
		}
			
		
		
	}
	static class Tuple3<T1,T2, T3> extends Tuple2<T1,T2> implements Serializable{

		private static final long serialVersionUID = -6071821569179484377L;
		public final T3 t3;
		
		public Tuple3(T1 t1, T2 t2, T3 t3) {
			super(t1, t2);
			this.t3 = t3;
		}		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((t3 == null) ? 0 : t3.hashCode());
			return result;
		}			
		@Override
		public boolean equals(Object obj) {
			boolean result = super.equals(obj);
			if(!result) return result;
			Tuple3 other = (Tuple3) obj;
			if (t3 == null) {
				if (other.t3 != null)
					return false;
			} else if (!t3.equals(other.t3))
				return false;
			return true;
		}
		@Override
		public String toString() {
			return super.toString()+"[t3=" + t3 + "]";
		}
		
	}
	static class Tuple4<T1,T2, T3, T4> extends Tuple3<T1,T2, T3> implements Serializable{
		public final T4 t4;
		private static final long serialVersionUID = 6071821500079484377L;
		public Tuple4(T1 t1, T2 t2, T3 t3, T4 t4) {
			super(t1, t2, t3);
			this.t4 = t4;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((t4 == null) ? 0 : t4.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			boolean result = super.equals(obj);
			if(!result) return result;
			Tuple4 other = (Tuple4) obj;
			if (t4 == null) {
				if (other.t4 != null)
					return false;
			} else if (!t4.equals(other.t4))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return super.toString()+ "[t4=" + t4 + "]";
		}		
		
	}

	static class Tuple5<T1,T2, T3, T4, T5> extends Tuple4<T1,T2, T3, T4> implements Serializable{
		public final T5 t5;
		private static final long serialVersionUID = 607185687079488967L;
		public Tuple5(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
			super(t1, t2, t3, t4);
			this.t5 = t5;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((t5 == null) ? 0 : t5.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			boolean result = super.equals(obj);
			if(!result) return result;
			Tuple5 other = (Tuple5) obj;
			if (t5 == null) {
				if (other.t5 != null)
					return false;
			} else if (!t5.equals(other.t5))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return super.toString()+ "[t5=" + t5 + "]";
		}

	}

	static class Tuple6<T1,T2, T3, T4, T5, T6> extends Tuple5<T1, T2, T3, T4, T5> implements Serializable{
		public final T6 t6;
		private static final long serialVersionUID = 60718865979488967L;
		public Tuple6(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6) {
			super(t1, t2, t3, t4, t5);
			this.t6 = t6;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((t6 == null) ? 0 : t6.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			boolean result = super.equals(obj);
			if(!result) return result;
			Tuple6 other = (Tuple6) obj;
			if (t6 == null) {
				if (other.t6 != null)
					return false;
			} else if (!t6.equals(other.t6))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return super.toString()+ "[t6=" + t6 + "]";
		}

	}

	static class Tuple7<T1,T2, T3, T4, T5, T6, T7> extends Tuple6<T1, T2, T3, T4, T5, T6> implements Serializable{
		public final T7 t7;
		private static final long serialVersionUID = 607181186598788967L;
		public Tuple7(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7) {
			super(t1, t2, t3, t4, t5, t6);
			this.t7 = t7;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((t7 == null) ? 0 : t7.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			boolean result = super.equals(obj);
			if(!result) return result;
			Tuple7 other = (Tuple7) obj;
			if (t7 == null) {
				if (other.t7 != null)
					return false;
			} else if (!t7.equals(other.t7))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return super.toString()+ "[t7=" + t7 + "]";
		}

	}

}

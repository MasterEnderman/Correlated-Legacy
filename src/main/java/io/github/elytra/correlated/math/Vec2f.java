package io.github.elytra.correlated.math;

public class Vec2f {
	public float x;
	public float y;
	public Vec2f(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Vec2f add(float x, float y) {
		return new Vec2f(this.x+x, this.y+y);
	}
	
	public Vec2f addMut(float x, float y) {
		this.x += x;
		this.y += y;
		return this;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Vec2f other = (Vec2f) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x)) {
			return false;
		}
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return x+", "+y;
	}

	public Vec2f copy() {
		return new Vec2f(x, y);
	}
	
}

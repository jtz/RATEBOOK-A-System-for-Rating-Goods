package goods.util;

public class Pair<F, S> {
	private F first;
	private S second;
	
	public Pair(F first, S second) {
		super();
		this.first = first;
		this.second = second;
	}
	
	public Pair() {
		super();
	}

	public F getFirst() {
		return first;
	}

	public void setFirst(F first) {
		this.first = first;
	}

	public S getSecond() {
		return second;
	}

	public void setSecond(S second) {
		this.second = second;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o instanceof Pair) {
            @SuppressWarnings("rawtypes")
			Pair pair = (Pair) o;
            if (first != null ? !first.equals(pair.first) : pair.first != null)
                return false;
            if (second != null ? !second.equals(pair.second) : pair.second != null)
                return false;
            return true;
        }
        return false;
	}

	@Override
	public String toString() {
		return "Pair [first=" + first + ", second=" + second + "]";
	}
}

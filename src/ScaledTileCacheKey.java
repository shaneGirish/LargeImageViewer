public class ScaledTileCacheKey {
	public final Tile tile;
	public final Double scale;
	
	public ScaledTileCacheKey(Double scale, Tile tile) {
		this.scale = scale;
		this.tile = tile;
	}
	
	private Integer getKey(double scale) {
		return (int) Math.floor(scale * 1000);		
	}

	@Override public int hashCode() {
		final int prime = 53;
		int result = 1;
		result = prime * result + ((scale == null) ? 0 : getKey(scale));
		result = prime * result + ((tile == null) ? 0 : tile.hashCode());
		return result;
	}

	@Override public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		ScaledTileCacheKey other = (ScaledTileCacheKey) obj;
		if (!getKey(scale).equals(getKey(other.scale)))
			return false;
		if (!tile.equals(other.tile))
			return false;
		return true;
	}
}

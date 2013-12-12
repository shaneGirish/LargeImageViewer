import java.awt.Dimension;

public class ScaledTileCacheKey {
	public final Tile tile;
	public final Dimension dimension;
	
	public ScaledTileCacheKey(Dimension dimension, Tile tile) {
		this.dimension = dimension;
		this.tile = tile;
	}

	@Override public int hashCode() {
		final int prime = 53;
		int result = 1;
		result = prime * result + ((dimension == null) ? 0 : dimension.hashCode());
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
		if (!dimension.equals(dimension))
			return false;
		if (!tile.equals(other.tile))
			return false;
		return true;
	}
}

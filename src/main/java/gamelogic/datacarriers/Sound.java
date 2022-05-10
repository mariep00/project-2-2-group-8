package gamelogic.datacarriers;

public record Sound(double angle, double loudness) {
    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other.getClass() == this.getClass()) {
            Sound temp = (Sound) other;
            return temp.angle() == this.angle() && temp.loudness() == this.loudness();
        }
        return false;
    }
}

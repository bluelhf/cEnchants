package io.github.bluelhf.cenchants.utilities;

import org.bukkit.util.Vector;

public class Quaternion implements Cloneable {
    double x, y, z, r;

    public Quaternion(double x, double y, double z, double r) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.r = r;
    }

    public Quaternion(double[] coords) {
        if (coords.length != 4)
            throw new IllegalArgumentException("Length of coordinates was " + coords.length + ", but a Quaternion needs 4.");
        this.x = coords[0];
        this.y = coords[1];
        this.z = coords[2];
        this.r = coords[3];
    }

    public Quaternion(Vector v, double yaw, boolean degrees) {
        if (degrees) yaw *= Math.PI / 360;
        double a = Math.sin(yaw);
        this.x = v.getX() * a;
        this.y = v.getY() * a;
        this.z = v.getZ() * a;
        this.r = Math.cos(yaw);
    }

    public Quaternion conjugate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        return this;
    }

    public Quaternion multiply(Quaternion other) {
        double tX = this.x;
        double tY = this.y;
        double tZ = this.z;
        double tR = this.r;
        double oX = other.x;
        double oY = other.y;
        double oZ = other.z;
        double oR = other.r;

        this.x = tR * oX + tX * oR + tY * oZ - tZ * oY;
        this.y = tR * oY - tX * oZ + tY * oR + tZ * oX;
        this.z = tR * oZ + tX * oY - tY * oX + tZ * oR;
        this.r = tR * oR - tX * oX - tY * oY - tZ * oZ;

        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null) return false;
        if (!(obj instanceof Quaternion)) return false;
        Quaternion other = (Quaternion)obj;
        if (this.x != other.x) return false;
        if (this.y != other.y) return false;
        if (this.z != other.z) return false;
        return this.r == other.r;
    }

    @Override
    public Quaternion clone() {
        try {
            return (Quaternion) super.clone();
        } catch (CloneNotSupportedException ex) {
            return new Quaternion(this.x, this.y, this.z, this.r);
        }
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getR() {
        return r;
    }
    public Quaternion setX(double x) {
        this.x = x;
        return this;
    }
    public Quaternion setY(double y) {
        this.y = y;
        return this;
    }
    public Quaternion setZ(double z) {
        this.z = z;
        return this;
    }
    public Quaternion setR(double r) {
        this.r = r;
        return this;
    }
    public Quaternion add(Quaternion other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        this.r += other.r;
        return this;
    }
    public Quaternion add(double x, double y, double z, double r) {
        this.x += x;
        this.y += y;
        this.z += z;
        this.r += r;
        return this;
    }
    public Quaternion subtract(Quaternion other) {
        return this.multiply(other.clone().conjugate());
    }
    public Quaternion subtract(double x, double y, double z, double r) {
        Quaternion other = new Quaternion(x, y, z, r);
        return this.multiply(other.conjugate());
    }

    public Vector toVector() {
        return new Vector(this.x, this.y, this.z);
    }

    public static Vector transformByQuaternion(Vector vector, Quaternion quaternion) {
        Quaternion clone = quaternion.clone();
        clone.multiply(new Quaternion(vector.getX(), vector.getY(), vector.getZ(), 0));
        Quaternion otherClone = quaternion.clone();
        otherClone.conjugate();
        clone.multiply(otherClone);
        return new Vector(clone.x, clone.y, clone.z);
    }
}

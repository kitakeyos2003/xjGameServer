// 
// Decompiled by Procyon v0.5.36
// 
package hero.pet;

public class PetPK {

    public short kind;
    public short stage;
    public short type;

    public PetPK() {
    }

    public PetPK(final short kind, final short stage, final short type) {
        this.kind = kind;
        this.stage = stage;
        this.type = type;
    }

    public short getKind() {
        return this.kind;
    }

    public void setKind(final short kind) {
        this.kind = kind;
    }

    public short getStage() {
        return this.stage;
    }

    public void setStage(final short stage) {
        this.stage = stage;
    }

    public short getType() {
        return this.type;
    }

    public void setType(final short type) {
        this.type = type;
    }

    public boolean is0() {
        return this.kind == 0 && this.stage == 0 && this.type == 0;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + this.kind;
        result = 31 * result + this.stage;
        result = 31 * result + this.type;
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        PetPK other = (PetPK) obj;
        return this.kind == other.kind && this.stage == other.stage && this.type == other.type;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getKind()).append(this.getStage()).append(this.getType());
        return sb.toString();
    }

    public int intValue() {
        return Integer.parseInt(this.toString());
    }
}

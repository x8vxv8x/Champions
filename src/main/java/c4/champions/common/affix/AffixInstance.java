package c4.champions.common.affix;

import net.minecraft.nbt.NBTTagCompound;

public class AffixInstance {

    private final Affix affix;
    private final AffixState state;

    public AffixInstance(Affix affix) {
        this.affix = affix;
        this.state = affix.createState();
    }

    public Affix getAffix() {
        return affix;
    }

    public AffixState getState() {
        return state;
    }

    public String getIdentifier() {
        return affix.getIdentifier();
    }

    public NBTTagCompound serializeState() {
        NBTTagCompound tag = new NBTTagCompound();
        state.write(tag);
        return tag;
    }

    public void deserializeState(NBTTagCompound tag) {
        state.read(tag);
    }
}

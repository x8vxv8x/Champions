package c4.champions.common.affix;

import net.minecraft.nbt.NBTTagCompound;

public interface AffixState {

    AffixState EMPTY = new AffixState() {
        @Override
        public void read(NBTTagCompound tag) {
        }

        @Override
        public void write(NBTTagCompound tag) {
        }
    };

    void read(NBTTagCompound tag);

    void write(NBTTagCompound tag);
}

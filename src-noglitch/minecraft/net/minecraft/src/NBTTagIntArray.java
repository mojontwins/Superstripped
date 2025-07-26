package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class NBTTagIntArray extends NBTBase {
	public int[] field_48181_a;

	public NBTTagIntArray(String string1) {
		super(string1);
	}

	public NBTTagIntArray(String string1, int[] i2) {
		super(string1);
		this.field_48181_a = i2;
	}

	void write(DataOutput dataOutput1) throws IOException {
		dataOutput1.writeInt(this.field_48181_a.length);

		for(int i2 = 0; i2 < this.field_48181_a.length; ++i2) {
			dataOutput1.writeInt(this.field_48181_a[i2]);
		}

	}

	void load(DataInput dataInput1) throws IOException {
		int i2 = dataInput1.readInt();
		this.field_48181_a = new int[i2];

		for(int i3 = 0; i3 < i2; ++i3) {
			this.field_48181_a[i3] = dataInput1.readInt();
		}

	}

	public byte getId() {
		return (byte)11;
	}

	public String toString() {
		return "[" + this.field_48181_a.length + " bytes]";
	}

	public NBTBase copy() {
		int[] i1 = new int[this.field_48181_a.length];
		System.arraycopy(this.field_48181_a, 0, i1, 0, this.field_48181_a.length);
		return new NBTTagIntArray(this.getName(), i1);
	}

	public boolean equals(Object object1) {
		if(!super.equals(object1)) {
			return false;
		} else {
			NBTTagIntArray nBTTagIntArray2 = (NBTTagIntArray)object1;
			return this.field_48181_a == null && nBTTagIntArray2.field_48181_a == null || this.field_48181_a != null && this.field_48181_a.equals(nBTTagIntArray2.field_48181_a);
		}
	}

	public int hashCode() {
		return super.hashCode() ^ Arrays.hashCode(this.field_48181_a);
	}
}

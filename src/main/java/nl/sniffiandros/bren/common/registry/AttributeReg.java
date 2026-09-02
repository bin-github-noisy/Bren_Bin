package nl.sniffiandros.bren.common.registry;

import nl.sniffiandros.bren.common.Bren;

import java.util.UUID;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class AttributeReg {
    public static Attribute RANGED_DAMAGE;
    public static Attribute FIRE_RATE;
    public static Attribute RECOIL;

    public static final UUID RANGED_DAMAGE_MODIFIER_ID = UUID.fromString("EF1BE063-D502-1F12-9E55-7D827281DB27");
    public static final UUID FIRE_RATE_MODIFIER_ID = UUID.fromString("C8E578CC-5986-417E-B78D-CD4F6F3535CD");
    public static final UUID RECOIL_MODIFIER_ID = UUID.fromString("EA3C78D6-F93E-45CC-B683-4EBF2E5DE456");

    public static void reg() {
        try {
            RANGED_DAMAGE = new RangedAttribute("attribute.name.ranged_damage", 0d, 0d, 2048d).setSyncable(true);
            Registry.register(BuiltInRegistries.ATTRIBUTE, Identifier.fromNamespaceAndPath(Bren.MODID, "ranged_damage"), RANGED_DAMAGE);
            
            FIRE_RATE = new RangedAttribute("attribute.name.fire_rate", 0d, 0d, 2048d).setSyncable(true);
            Registry.register(BuiltInRegistries.ATTRIBUTE, Identifier.fromNamespaceAndPath(Bren.MODID, "fire_rate"), FIRE_RATE);
            
            RECOIL = new RangedAttribute("attribute.name.recoil", 0d, -360d, 360d).setSyncable(true);
            Registry.register(BuiltInRegistries.ATTRIBUTE, Identifier.fromNamespaceAndPath(Bren.MODID, "recoil"), RECOIL);
        } catch (Exception e) {
            // 注册属性失败
        }
    }
}
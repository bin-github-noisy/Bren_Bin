package nl.sniffiandros.bren.common.utils;

import java.util.function.IntFunction;
import net.minecraft.util.ByIdMap;

public class GunHelper {
    // 移除所有TrackedData相关代码，改用实例字段存储状态

    public enum GunStates {
        NORMAL(0),
        RELOADING(1);


        final int id;

        private static final IntFunction<GunStates> BY_ID = ByIdMap.continuous(GunStates::getId, values(), ByIdMap.OutOfBoundsStrategy.CLAMP);

        GunStates(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }

        public static GunStates byIndex(int index) {
            return BY_ID.apply(index);
        }
    }
}
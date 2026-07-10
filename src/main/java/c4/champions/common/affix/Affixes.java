package c4.champions.common.affix;

import c4.champions.common.affix.affix.*;
import c4.champions.common.affix.Affix;

public class Affixes {

    public static void registerAffixes() {
        register(
                new AffixMolten(),
                new AffixShielding(),
                new AffixReflecting(),
                new AffixVortex(),
                new AffixDampening(),
                new AffixInfested(),
                new AffixJailer(),
                new AffixArctic(),
                new AffixDesecrator(),
                new AffixHasty(),
                new AffixLively(),
                new AffixPlagued(),
                new AffixReality(),
                new AffixAdaptable(),
                new AffixScrapper(),
                new AffixKnockback(),
                new AffixShadow(),
                new AffixDispel(),
                new AffixCinder());
    }

    private static void register(Affix... affixes) {
        for (Affix affix : affixes) {
            AffixRegistry.registerAffix(affix);
        }
    }
}

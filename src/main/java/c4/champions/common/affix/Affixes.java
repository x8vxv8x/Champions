package c4.champions.common.affix;

import c4.champions.common.affix.affix.AffixAdaptable;
import c4.champions.common.affix.affix.AffixArctic;
import c4.champions.common.affix.affix.AffixCinder;
import c4.champions.common.affix.affix.AffixDampening;
import c4.champions.common.affix.affix.AffixDesecrator;
import c4.champions.common.affix.affix.AffixHasty;
import c4.champions.common.affix.affix.AffixInfested;
import c4.champions.common.affix.affix.AffixJailer;
import c4.champions.common.affix.affix.AffixKnockback;
import c4.champions.common.affix.affix.AffixLively;
import c4.champions.common.affix.affix.AffixMolten;
import c4.champions.common.affix.affix.AffixPlagued;
import c4.champions.common.affix.affix.AffixReality;
import c4.champions.common.affix.affix.AffixReflecting;
import c4.champions.common.affix.affix.AffixScrapper;
import c4.champions.common.affix.affix.AffixShielding;
import c4.champions.common.affix.affix.AffixVortex;
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
                new AffixCinder());
    }

    private static void register(Affix... affixes) {
        for (Affix affix : affixes) {
            AffixRegistry.registerAffix(affix);
        }
    }
}

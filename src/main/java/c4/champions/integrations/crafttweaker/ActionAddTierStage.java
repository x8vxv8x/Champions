package c4.champions.integrations.crafttweaker;

import c4.champions.integrations.gamestages.ChampionStages;
import crafttweaker.IAction;

public class ActionAddTierStage implements IAction {

    private final String stage;
    private final int tier;
    private final int dimension;
    private final boolean dimensional;

    public ActionAddTierStage(String stage, int tier) {
        this(stage, tier, 0, false);
    }

    public ActionAddTierStage(String stage, int tier, int dimension) {
        this(stage, tier, dimension, true);
    }

    public ActionAddTierStage(String stage, int tier, int dimension, boolean dimensional) {
        this.stage = stage;
        this.tier = tier;
        this.dimension = dimension;
        this.dimensional = dimensional;
    }

    @Override
    public void apply() {

        if (this.dimensional) {
            ChampionStages.addTierStage(tier, stage, dimension);
        } else {
            ChampionStages.addTierStage(tier, stage);
        }
    }

    @Override
    public String describe() {
        return "Adding tier " + this.tier + " to stage " + this.stage + (this.dimensional ? " for dimension " + this.dimension : "");
    }
}

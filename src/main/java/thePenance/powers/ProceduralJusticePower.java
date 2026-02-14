package thePenance.powers;

import com.megacrit.cardcrawl.core.AbstractCreature;
import thePenance.PenanceMod;
import thePenance.character.PenanceDifficultyHelper;

public class ProceduralJusticePower extends BasePower {
    public static final String POWER_ID = PenanceMod.makeID("ProceduralJusticePower");

    public ProceduralJusticePower(AbstractCreature owner, int amount) {
        super(POWER_ID, PowerType.BUFF, false, owner, amount);
    }

    @Override
    public void updateDescription() {
        if (PenanceDifficultyHelper.currentDifficulty == PenanceDifficultyHelper.DifficultyLevel.HELL) {
            // Hell 难度显示：每当你获得格挡时，获得 2/4/6 点屏障。
            this.description = DESCRIPTIONS[1] + (2 * amount) + DESCRIPTIONS[2];
        } else {
            // 普通难度显示：每当你获得格挡时，获得等量的屏障。(不显示层数)
            this.description = DESCRIPTIONS[0];
        }
    }
}
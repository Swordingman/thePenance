package thePenance.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import thePenance.PenanceMod;
import thePenance.relics.LetterOfGratitude;

public class BarrierPower extends BasePower implements CloneablePowerInterface {
    public static final String POWER_ID = PenanceMod.makeID("BarrierPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public BarrierPower(AbstractCreature owner, int amount) {
        // 使用你的 BasePower 构造函数，或者标准的 super
        // 这里保留你之前的参数结构
        super(POWER_ID, PowerType.BUFF, false, owner, amount);

        // 直接赋值，不再计算敏捷
        this.amount = amount;
        this.canGoNegative = false;

        updateDescription();
    }

    @Override
    public void stackPower(int stackAmount) {
        this.fontScale = 8.0F;

        if (owner.isPlayer && ((AbstractPlayer) owner).hasRelic(LetterOfGratitude.ID)) {
            stackAmount += Math.max(1, (int)(stackAmount * 0.2f));
        }

        this.amount += stackAmount;
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @Override
    public AbstractPower makeCopy() {
        return new BarrierPower(owner, amount);
    }
}
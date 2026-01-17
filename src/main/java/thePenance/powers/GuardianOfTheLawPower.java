package thePenance.powers;

import basemod.interfaces.CloneablePowerInterface;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import thePenance.PenanceMod;

public class GuardianOfTheLawPower extends AbstractPower implements CloneablePowerInterface {
    public static final String POWER_ID = PenanceMod.makeID("GuardianOfTheLawPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

    public GuardianOfTheLawPower(AbstractCreature owner, int amount) {
        this.name = NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = amount;
        this.type = PowerType.BUFF;
        this.loadRegion("juggernaut"); // 暂时借用"主宰"图标，或者用其他
        updateDescription();
    }

    // --- 当屏障受到伤害时调用 ---
    public void onBarrierDamaged() {
        this.flash();
        // 获得裁决
        addToBot(new ApplyPowerAction(owner, owner, new JudgementPower(owner, this.amount), this.amount));
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1];
    }

    @Override
    public AbstractPower makeCopy() {
        return new GuardianOfTheLawPower(owner, amount);
    }
}
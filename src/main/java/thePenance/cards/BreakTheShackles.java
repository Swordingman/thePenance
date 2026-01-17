package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.StrengthPower;
import thePenance.character.Penance;
import thePenance.powers.BarrierPower;
import thePenance.util.CardStats;

public class BreakTheShackles extends BaseCard {

    public static final String ID = makeID("BreakTheShackles");

    private static final int COST = 1;
    private static final int UPG_COST = 0; // 升级后变 0 费

    // 我们用 MagicNumber 来表示“每消耗多少点屏障”
    // 这样描述里可以写：每消耗 !M! 点...
    private static final int THRESHOLD = 5;

    public BreakTheShackles() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.SKILL,
                CardRarity.UNCOMMON,
                CardTarget.SELF,
                COST
        ));

        this.exhaust = true;

        setCostUpgrade(UPG_COST);
        setMagic(THRESHOLD);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 检查是否有屏障
        if (p.hasPower(BarrierPower.POWER_ID)) {
            // 1. 获取当前屏障总量
            int currentBarrier = p.getPower(BarrierPower.POWER_ID).amount;

            // 2. 计算倍率 (向下取整，例如 14点屏障 / 5 = 2倍)
            int multiplier = currentBarrier / this.magicNumber;

            // 3. 消耗所有屏障 (移除Buff)
            addToBot(new RemoveSpecificPowerAction(p, p, BarrierPower.POWER_ID));

            // 4. 发放奖励
            if (multiplier > 0) {
                // 获得力量
                addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, multiplier), multiplier));
                // 获得能量
                addToBot(new GainEnergyAction(multiplier));
            }
        }
    }
}
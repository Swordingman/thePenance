package thePenance.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.relics.ChemicalX;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import thePenance.powers.BarrierPower;
import thePenance.powers.JudgementPower;

public class ResoluteAction extends AbstractGameAction {
    private final boolean freeToPlayOnce;
    private final AbstractPlayer p;
    private final int energyOnUse;
    private final boolean upgraded;
    private final int barrierMultiplier;

    public ResoluteAction(AbstractPlayer p, int barrierMultiplier, boolean upgraded, boolean freeToPlayOnce, int energyOnUse) {
        this.p = p;
        this.barrierMultiplier = barrierMultiplier; // 屏障的倍率 (4 或 5)
        this.upgraded = upgraded;                   // 是否升级 (用于判断裁决是否+1)
        this.freeToPlayOnce = freeToPlayOnce;       // 是否免费打出 (如通过其他卡牌效果)
        this.energyOnUse = energyOnUse;             // 打出时的能量
        this.duration = Settings.ACTION_DUR_XFAST;
        this.actionType = ActionType.SPECIAL;
    }

    @Override
    public void update() {
        // 计算 X 的数值
        int effect = EnergyPanel.totalCount;
        if (this.energyOnUse != -1) {
            effect = this.energyOnUse;
        }

        // 如果拥有遗物“化学物 X”，X 值 +2
        if (p.relics != null) {
            for (com.megacrit.cardcrawl.relics.AbstractRelic r : p.relics) {
                if (ChemicalX.ID.equals(r.relicId)) {
                    effect += 2;
                    r.flash();
                }
            }
        }

        if (effect > 0 || upgraded) {
            // 1. 计算屏障：X * 倍率
            int barrierAmt = effect * barrierMultiplier;
            if (barrierAmt > 0) {
                addToBot(new ApplyPowerAction(p, p, new BarrierPower(p, barrierAmt), barrierAmt));
            }

            // 2. 计算裁决：X (+1 如果升级)
            // 裁决的基础倍率是 1:1，所以直接用 effect
            int judgementAmt = effect;
            if (upgraded) {
                judgementAmt += 1; // 升级后 X+1
            }

            if (judgementAmt > 0) {
                addToBot(new ApplyPowerAction(p, p, new JudgementPower(p, judgementAmt), judgementAmt));
            }
        }

        // 消耗能量 (如果不是免费打出)
        if (!this.freeToPlayOnce) {
            this.p.energy.use(EnergyPanel.totalCount);
        }

        this.isDone = true;
    }
}
package thePenance.relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.RelicAboveCreatureAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import thePenance.PenanceMod;
import thePenance.powers.JustifiedDefensePower;

public class GoldenScales extends BaseRelic {
    public static final String ID = PenanceMod.makeID("GoldenScales");

    private boolean discardedAttackThisTurn = false;

    public GoldenScales() {
        super(ID, "GoldenScales", RelicTier.UNCOMMON, LandingSound.CLINK);
    }

    @Override
    public void atTurnStart() {
        discardedAttackThisTurn = false;
        this.pulse = false;
    }

    // 由 Patch 调用 (处理手动丢弃，如"自证其罪")
    public void onAttackDiscarded() {
        discardedAttackThisTurn = true;
        this.pulse = true; // 闪烁提示回合结束会生效
    }

    @Override
    public void onPlayerEndTurn() {
        // 1. 检查是否已经手动丢过
        boolean trigger = discardedAttackThisTurn;

        // 2. 检查手牌中是否有即将被自动丢弃的攻击牌
        // (非保留、非固有保留)
        if (!trigger) {
            for (AbstractCard c : AbstractDungeon.player.hand.group) {
                if (c.type == AbstractCard.CardType.ATTACK && !c.retain && !c.selfRetain) {
                    trigger = true;
                    break;
                }
            }
        }

        if (trigger) {
            this.flash();
            addToBot(new RelicAboveCreatureAction(AbstractDungeon.player, this));
            // 获得 1 点正当防卫
            addToBot(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player,
                    new JustifiedDefensePower(AbstractDungeon.player, 1), 1));
        }
    }
}
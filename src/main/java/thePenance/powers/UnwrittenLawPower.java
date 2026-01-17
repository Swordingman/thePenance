package thePenance.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.PenanceMod;

import java.util.UUID;

public class UnwrittenLawPower extends BasePower {
    public static final String POWER_ID = PenanceMod.makeID("UnwrittenLawPower");

    // 目标卡牌的唯一ID
    private final UUID targetUuid;

    public UnwrittenLawPower(AbstractCreature owner, UUID targetUuid) {
        super(POWER_ID, PowerType.BUFF, false, owner, 1);
        this.targetUuid = targetUuid;
        this.loadRegion("double_tap"); // 借用双发图标
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        // 检查打出的卡是否是我们要找的那张
        if (!card.purgeOnUse && card.uuid.equals(targetUuid)) {
            this.flash();

            // 创建一个副本进行打出
            AbstractCard tmp = card.makeSameInstanceOf();
            AbstractDungeon.player.limbo.addToBottom(tmp);
            tmp.current_x = card.current_x;
            tmp.current_y = card.current_y;
            tmp.target_x = (float) Settings.WIDTH / 2.0F - 300.0F * Settings.scale;
            tmp.target_y = (float) Settings.HEIGHT / 2.0F;

            if (tmp.cost > 0) {
                tmp.freeToPlayOnce = true;
            }

            if (action.target != null) {
                if (action.target instanceof AbstractMonster) {
                    tmp.calculateCardDamage((AbstractMonster) action.target);
                }
                else {
                    tmp.applyPowers();
                }
            }

            tmp.purgeOnUse = true;

            // 加入打出队列
            AbstractDungeon.actionManager.addToBottom(new NewQueueCardAction(tmp, action.target, false, true));

            // 触发后移除自身（因为只生效一次）
            addToBot(new RemoveSpecificPowerAction(owner, owner, this));
        }
    }

    @Override
    public void atEndOfTurn(boolean isPlayer) {
        // 回合结束移除（仅限本回合）
        addToBot(new RemoveSpecificPowerAction(owner, owner, this));
    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0];
    }
}
package thePenance.powers;

import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;
import thePenance.PenanceMod;

public class PlayerFacingPower extends AbstractPower {
    public static final String POWER_ID = PenanceMod.makeID("PlayerFacingPower");
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);

    // false = 面朝右 (默认), true = 面朝左
    public boolean facingLeft = false;

    public PlayerFacingPower(AbstractCreature owner) {
        this.name = powerStrings.NAME;
        this.ID = POWER_ID;
        this.owner = owner;
        this.amount = -1;
        this.type = PowerType.BUFF;
        // 建议使用原版 Surrounded 的图标，或者自己画一个
        this.loadRegion("surrounded");
        updateDescription();
    }

    @Override
    public void updateDescription() {
        this.description = powerStrings.DESCRIPTIONS[0];
    }

    @Override
    public void onUseCard(AbstractCard card, UseCardAction action) {
        // 只有单体攻击卡会改变朝向
        if (card.type == AbstractCard.CardType.ATTACK && action.target != null) {
            float targetX = action.target.drawX;
            float playerX = owner.drawX;

            if (targetX < playerX) {
                // 攻击左边的敌人 -> 转身向左
                if (!facingLeft) {
                    facingLeft = true;
                    updateFacingVisuals();
                }
            } else {
                // 攻击右边的敌人 -> 转身向右
                if (facingLeft) {
                    facingLeft = false;
                    updateFacingVisuals();
                }
            }
        }
    }

    // 更新角色视觉朝向
    public void updateFacingVisuals() {
        owner.flipHorizontal = facingLeft;
    }

    @Override
    public void onInitialApplication() {
        facingLeft = false;
        owner.flipHorizontal = false;
    }

    @Override
    public void onRemove() {
        owner.flipHorizontal = false;
    }

    /**
     * 判断玩家是否背对某个 X 坐标
     * @param enemyX 敌人的 X 坐标
     * @return true 如果玩家背对敌人
     */
    public boolean isBackExposedTo(float enemyX) {
        if (enemyX < owner.drawX) {
            // 敌人在左边。如果玩家面朝右(false)，则背对左边。
            return !facingLeft;
        } else {
            // 敌人在右边。如果玩家面朝左(true)，则背对右边。
            return facingLeft;
        }
    }
}
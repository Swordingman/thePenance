package thePenance.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import thePenance.powers.BarrierPower;

public class BlockToBarrierAction extends AbstractGameAction {
    private final AbstractPlayer p;

    public BlockToBarrierAction() {
        this.p = AbstractDungeon.player;
        this.duration = Settings.ACTION_DUR_FAST;
        this.actionType = ActionType.BLOCK; // 这是一个与防御相关的动作
    }

    @Override
    public void update() {
        // 动作执行时，获取玩家当前的格挡值
        // 这里之所以不在构造函数里获取，是因为卡牌打出时，前面的卡可能还没生效
        if (this.duration == Settings.ACTION_DUR_FAST) {
            int currentBlock = this.p.currentBlock;

            if (currentBlock > 0) {
                // 1. 失去所有格挡
                this.p.loseBlock();

                // 2. 获得等量的屏障
                // 注意：这里使用 addToTop 是为了保证视觉效果紧凑，addToBot 也可以
                addToTop(new ApplyPowerAction(this.p, this.p, new BarrierPower(this.p, currentBlock), currentBlock));
            }
        }

        this.tickDuration();
    }
}
package thePenance.powers;

import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.SlowPower;
import thePenance.PenanceMod;

public class LoseSlowPower extends BasePower {
    public static final String POWER_ID = PenanceMod.makeID("LoseSlowPower");

    public LoseSlowPower(AbstractCreature owner, int amount) {
        // initDescription = true (我们需要显示描述)
        // loadImage = false (因为我们要手动加载原版图标，不让BasePower去找自定义图片)
        super(POWER_ID, PowerType.BUFF, true, owner, owner, amount, true, false);

        // 手动借用原版“缓慢”的图标
        this.loadRegion("slow");
    }

    @Override
    public void updateDescription() {
        // 直接使用本地化文件中的描述
        // "回合开始时，移除缓慢。"
        this.description = DESCRIPTIONS[0];
    }

    @Override
    public void atStartOfTurn() {
        this.flash(); // 触发时闪烁一下，提醒玩家生效了
        // 移除缓慢
        addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, SlowPower.POWER_ID));
        // 移除本能力
        addToBot(new RemoveSpecificPowerAction(this.owner, this.owner, this));
    }
}
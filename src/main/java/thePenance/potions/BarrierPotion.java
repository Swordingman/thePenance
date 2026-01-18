package thePenance.potions;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import thePenance.PenanceMod;
import thePenance.powers.BarrierPower;

public class BarrierPotion extends BasePotion {

    public static final String ID = PenanceMod.makeID("BarrierPotion");

    // 基础数值
    private static final int POTENCY = 12;

    public BarrierPotion() {
        super(
                ID,
                POTENCY,
                PotionRarity.COMMON, // 稀有度：普通 (参考原版格挡药水)
                PotionSize.ANVIL,    // 形状：铁砧形 (参考原版格挡药水)
                Color.SKY.cpy(),     // 液体颜色：天蓝色
                null,                // 混合色：无
                null                 // 斑点色：无
        );

        // 如果你想让它看起来像原版蓝色药水，也可以用下面这个构造函数：
        // super(ID, POTENCY, PotionRarity.COMMON, PotionSize.ANVIL, PotionColor.BLUE);
    }

    @Override
    public void use(AbstractCreature target) {
        // 获取玩家实例
        target = AbstractDungeon.player;

        // 只有在战斗房间内才生效 (虽然药水通常只能在战斗用，加个判断更保险)
        if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
            this.addToBot(new ApplyPowerAction(target, target, new BarrierPower(target, this.potency), this.potency));
        }
    }

    @Override
    public String getDescription() {
        // 从本地化文件中读取描述
        // 假设 JSON 格式为: "DESCRIPTIONS": ["获得 #b", " 层 #y屏障 。"]
        return DESCRIPTIONS[0] + this.potency + DESCRIPTIONS[1];
    }
}
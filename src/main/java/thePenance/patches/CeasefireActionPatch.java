package thePenance.patches;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import javassist.CannotCompileException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import thePenance.powers.CeasefirePower;

@SpirePatch(
        clz = GameActionManager.class,
        method = "getNextAction"
)
public class CeasefireActionPatch {

    // 使用 Instrument Patch 来拦截方法调用
    public static ExprEditor Instrument() {
        return new ExprEditor() {
            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                // 找到代码中调用 "takeTurn" 的那一行
                if (m.getClassName().equals(AbstractMonster.class.getName())
                        && m.getMethodName().equals("takeTurn")) {

                    // 把那一行替换成我们要的逻辑：
                    // $0 代表调用该方法的对象（也就是怪物实例）
                    // $proceed($$) 代表执行原来的方法
                    m.replace(
                            "if (thePenance.patches.CeasefireActionPatch.shouldSkipTurn($0)) {" +
                                    "    ;" +
                                    "} else {" +
                                    "    $proceed($$);" + // 正常执行 takeTurn
                                    "}"
                    );
                }
            }
        };
    }

    public static boolean shouldSkipTurn(AbstractMonster m) {
        if (m.hasPower(CeasefirePower.POWER_ID) && m.getPower(CeasefirePower.POWER_ID).amount > 0) {
            if (m.getIntentBaseDmg() >= 0) {
                m.getPower(CeasefirePower.POWER_ID).flash();
                return true;
            }
        }
        return false;
    }
}
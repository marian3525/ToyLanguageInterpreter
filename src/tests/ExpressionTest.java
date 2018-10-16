package tests;

import controller.Controller;
import exceptions.SyntaxException;
import model.expression.Expression;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.runner.RunWith;

import java.util.Vector;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class ExpressionTest {
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addClass(Expression.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @org.junit.Test
    public void infixToPostfix() {
        String infix = "2*3-8/2";
        try {
            Vector<String> postfix = Expression.infixToPostfix(infix);
            assertArrayEquals(postfix.toArray(), new String[]{"2","3","*","8","2","/","-"});
        } catch (SyntaxException e) {
            assert(false);
        }
        infix = "2*3-8";
        try {
            Vector<String> postfix = Expression.infixToPostfix(infix);
            assertArrayEquals(postfix.toArray(), new String[]{"2","3","*","8","-"});
        } catch (SyntaxException e) {
            assert(false);
        }
    }

    @org.junit.Test
    public void buildExpressionFromPostfix() {
    }
}

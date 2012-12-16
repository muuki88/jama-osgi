package jama.rules;

import static org.junit.Assert.fail;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * 
 * @author Nepomuk Seiler
 * @see "http://junitext.sourceforge.net/tutorial.html"
 * @see "http://tux2323.blogspot.de/2009/10/junitext-prerequisite-as-junit-47-rule.html"
 * 
 */
public class PrerequisiteRule implements TestRule {

    @Override
    public Statement apply(final Statement base, final Description description) {
        final Prerequisite annotation = description.getAnnotation(Prerequisite.class);
        if (annotation == null) {
            return base;
        }

        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                boolean evaluate = true;
                for (Class<? extends IRequirement> req : annotation.value()) {
                    IRequirement requirement = req.newInstance();
                    if (!requirement.available() && annotation.skipOnFail()) {
                        evaluate = false;
                    } else if (!requirement.available()) {
                        fail("Test requires " + requirement.getClass().getSimpleName());
                    }
                }
                if (evaluate) {
                    base.evaluate();
                } else {
                    System.out.println("Warning: Skipping test " + description.getDisplayName());
                }
            }
        };
    }
}

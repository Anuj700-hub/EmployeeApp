package com.hungerbox.customer.bluetooth.Model;

import java.util.ArrayList;

public class SaveViolation {

    private ArrayList<Violation> violations ;

    public ArrayList<Violation> getViolations() {
        if(violations == null)
            return new ArrayList<>();
        return violations;
    }

    public void setViolations(ArrayList<Violation> violations) {
        this.violations = violations;
    }
}

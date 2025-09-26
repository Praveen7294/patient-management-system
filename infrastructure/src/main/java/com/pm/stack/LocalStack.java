package com.pm.stack;

import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.App;
import software.amazon.awscdk.AppProps;
import software.amazon.awscdk.BootstraplessSynthesizer;

public class LocalStack extends Stack {

    private final Vpc vpc;

    public LocalStack(final App scope, final String id, final StackProps props) {
        super(scope, id, props);

        this.vpc = createVpc();
    }

    private Vpc createVpc() {
        return Vpc.Builder.create(this, "PatientManagementVpc")
                .vpcName("PatientManagementVpc")
                .maxAzs(2)
                .build();
    }

    public static void main(String[] args) {
        App app = new App(AppProps.builder().outdir("./cdk.out").build());

        StackProps stackProps = StackProps.builder()
                .synthesizer(new BootstraplessSynthesizer())
                .build();

        new LocalStack(app, "localstack", stackProps);
        app.synth();
        System.out.println("App synthesizing in progress...");
    }
}

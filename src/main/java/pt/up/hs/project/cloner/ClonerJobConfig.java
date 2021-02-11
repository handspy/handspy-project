package pt.up.hs.project.cloner;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.SimpleJobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pt.up.hs.project.service.dto.BulkProjectPermissionDTO;

@EnableBatchProcessing
@Configuration
public class ClonerJobConfig {

    private final JobBuilderFactory jobBuilders;
    private final StepBuilderFactory stepBuilders;

    private final PermissionItemReader permissionItemReader;
    private final PermissionItemWriter permissionItemWriter;
    private final LabelItemReader labelItemReader;
    private final LabelItemWriter labelItemWriter;
    private final TaskItemReader taskItemReader;
    private final TaskItemWriter taskItemWriter;
    private final ParticipantItemReader participantItemReader;
    private final ParticipantItemWriter participantItemWriter;
    private final ProtocolCloningTasklet protocolCloningTasklet;
    private final TextCloningTasklet textCloningTasklet;
    private final ProjectRemovalTasklet projectRemovalTasklet;

    @Autowired
    public ClonerJobConfig(
        JobBuilderFactory jobBuilders,
        StepBuilderFactory stepBuilders,
        PermissionItemReader permissionItemReader,
        PermissionItemWriter permissionItemWriter,
        LabelItemReader labelItemReader,
        LabelItemWriter labelItemWriter,
        TaskItemReader taskItemReader,
        TaskItemWriter taskItemWriter,
        ParticipantItemReader participantItemReader,
        ParticipantItemWriter participantItemWriter,
        ProtocolCloningTasklet protocolCloningTasklet,
        TextCloningTasklet textCloningTasklet,
        ProjectRemovalTasklet projectRemovalTasklet
    ) {
        this.jobBuilders = jobBuilders;
        this.stepBuilders = stepBuilders;
        this.permissionItemReader = permissionItemReader;
        this.permissionItemWriter = permissionItemWriter;
        this.labelItemReader = labelItemReader;
        this.labelItemWriter = labelItemWriter;
        this.taskItemReader = taskItemReader;
        this.taskItemWriter = taskItemWriter;
        this.participantItemReader = participantItemReader;
        this.participantItemWriter = participantItemWriter;
        this.protocolCloningTasklet = protocolCloningTasklet;
        this.textCloningTasklet = textCloningTasklet;
        this.projectRemovalTasklet = projectRemovalTasklet;
    }

    public Job projectCloningJob(
        final boolean copyPermissions,
        final boolean move
    ) {
        SimpleJobBuilder jb = jobBuilders.get("projectCloningJob")
            .incrementer(new RunIdIncrementer())
            .start(labelCloningStep())
            .next(participantCloningStep())
            .next(taskCloningStep());
        if (copyPermissions) {
            jb.next(permissionCloningStep());
        }
        jb
            .next(protocolCloningStep())
            .next(textCloningStep());
        if (move) {
            jb.next(projectRemovalStep());
        }
        return jb
            .preventRestart()
            .build();
    }

    @Bean
    public Step permissionCloningStep() {
        return stepBuilders.get("permissionCloningStep")
            .<BulkProjectPermissionDTO, BulkProjectPermissionDTO>chunk(10)
            .reader(permissionItemReader)
            .writer(permissionItemWriter)
            .build();
    }

    @Bean
    public Step labelCloningStep() {
        return stepBuilders.get("labelCloningStep")
            .<Long, Long>chunk(10)
            .reader(labelItemReader)
            .writer(labelItemWriter)
            .build();
    }

    @Bean
    public Step taskCloningStep() {
        return stepBuilders.get("taskCloningStep")
            .<Long, Long>chunk(10)
            .reader(taskItemReader)
            .writer(taskItemWriter)
            .build();
    }

    @Bean
    public Step participantCloningStep() {
        return stepBuilders.get("participantCloningStep")
            .<Long, Long>chunk(10)
            .reader(participantItemReader)
            .writer(participantItemWriter)
            .build();
    }

    @Bean
    public Step protocolCloningStep() {
        return stepBuilders.get("protocolCloningStep")
            .tasklet(protocolCloningTasklet)
            .build();
    }

    @Bean
    public Step textCloningStep() {
        return stepBuilders.get("textCloningStep")
            .tasklet(textCloningTasklet)
            .build();
    }

    @Bean
    public Step projectRemovalStep() {
        return stepBuilders.get("projectRemovalStep")
            .tasklet(projectRemovalTasklet)
            .build();
    }
}

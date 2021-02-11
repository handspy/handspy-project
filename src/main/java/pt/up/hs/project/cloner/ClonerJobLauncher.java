package pt.up.hs.project.cloner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static pt.up.hs.project.cloner.ClonerConstants.*;

@Component
public class ClonerJobLauncher {

    private static final long EXECUTION_DELAY = 500;

    private static final ScheduledExecutorService executorService =
        Executors.newSingleThreadScheduledExecutor();

    private final Logger log = LoggerFactory.getLogger(ClonerJobLauncher.class);

    private final JobLauncher jobLauncher;
    private final ClonerJobConfig config;

    public ClonerJobLauncher(JobLauncher jobLauncher, ClonerJobConfig config) {
        this.jobLauncher = jobLauncher;
        this.config = config;
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void run(
        @Nonnull Long projectId,
        @Nonnull Long newProjectId,
        boolean copyPermissions,
        boolean move
    ) {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder
            .addLong(UNIQUENESS_PARAMETER, System.nanoTime())
            .addLong(PROJECT_ID_PARAMETER, projectId)
            .addLong(NEW_PROJECT_ID_PARAMETER, newProjectId)
            .addString(MOVE_PARAMETER, Boolean.toString(move));

        executorService.schedule(() -> {
            try {
                JobExecution jobExecution = jobLauncher.run(
                    config.projectCloningJob(copyPermissions, move),
                    jobParametersBuilder.toJobParameters()
                );
                log.info("Launched job: " + jobExecution.toString());
            } catch (Exception e) {
                log.error("Could not launch job: " + e.getMessage());
            }
        }, EXECUTION_DELAY, TimeUnit.MILLISECONDS);
    }
}

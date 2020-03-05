package pt.up.hs.project.service.importer.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Example;
import pt.up.hs.project.domain.Label;
import pt.up.hs.project.domain.Task;
import pt.up.hs.project.repository.LabelRepository;
import pt.up.hs.project.repository.TaskRepository;
import pt.up.hs.project.service.importer.batch.processor.ImportLoggingProcessor;
import pt.up.hs.project.utils.Dates;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class TaskCsvImportConfig {
    /*private static final String DEFAULT_DELIMITER = ",";
    private static final String DEFAULT_LABEL_DELIMITER = ";";

    private static final String HEADER_NAME = "name";
    private static final String HEADER_DESCRIPTION = "description";
    private static final String HEADER_STARTDATE = "startDate";
    private static final String HEADER_ENDDATE = "endDate";
    private static final String HEADER_LABELS = "labels";

    private static final String[] DEFAULT_HEADERS = new String[] {
        HEADER_NAME, HEADER_DESCRIPTION, HEADER_STARTDATE, HEADER_ENDDATE, HEADER_LABELS
    };

    private TaskRepository taskRepository;
    private LabelRepository labelRepository;

    @Autowired
    public TaskCsvImportConfig(
        TaskRepository taskRepository, LabelRepository labelRepository
    ) {
        this.taskRepository = taskRepository;
        this.labelRepository = labelRepository;
    }

    @Bean
    @StepScope
    ItemReader<Task> taskCsvImportItemReader(
        @Value("#{jobParameters['file.path']}") String pathToFile,
        @Value("#{jobParameters['delimiter']}") String delim,
        @Value("#{jobParameters['hasHeader']}") Boolean hasHeader
    ) {
        FlatFileItemReader<Task> csvFileReader = new FlatFileItemReader<>();
        csvFileReader.setResource(new FileSystemResource(pathToFile));
        csvFileReader.setLinesToSkip(hasHeader ? 1 : 0);

        LineMapper<Task> taskLineMapper = createTaskLineMapper(
            delim == null ? DEFAULT_DELIMITER : delim
        );
        csvFileReader.setLineMapper(taskLineMapper);

        return csvFileReader;
    }

    private LineMapper<Task> createTaskLineMapper(String delim) {
        DefaultLineMapper<Task> taskLineMapper = new DefaultLineMapper<>();

        LineTokenizer taskLineTokenizer = createTaskLineTokenizer(delim);
        taskLineMapper.setLineTokenizer(taskLineTokenizer);

        FieldSetMapper<Task> taskInformationMapper = createTaskInformationMapper();
        taskLineMapper.setFieldSetMapper(taskInformationMapper);

        return taskLineMapper;
    }

    private LineTokenizer createTaskLineTokenizer(String delim) {
        DelimitedLineTokenizer taskLineTokenizer = new DelimitedLineTokenizer();
        taskLineTokenizer.setDelimiter(delim);
        taskLineTokenizer.setNames(DEFAULT_HEADERS);
        taskLineTokenizer.setStrict(false);
        return taskLineTokenizer;
    }

    private FieldSetMapper<Task> createTaskInformationMapper() {
        return fs -> {
            Task task = new Task();
            task.setName(fs.readString(HEADER_NAME));
            task.setDescription(fs.readString(HEADER_DESCRIPTION));
            task.setStartDate(
                Dates.convertToLocalDate(fs.readDate(HEADER_STARTDATE))
            );
            task.setEndDate(
                Dates.convertToLocalDate(fs.readDate(HEADER_ENDDATE))
            );

            String labelNames = fs.readString(HEADER_LABELS);
            if (!labelNames.trim().isEmpty()) {
                task.setLabels(getLabelsByNames(labelNames.split(DEFAULT_LABEL_DELIMITER)));
            }
            return task;
        };
    }

    @Bean
    ItemProcessor<Task, Task> taskCsvImportLoggingProcessor() {
        return new ImportLoggingProcessor<>();
    }

    @Bean
    ItemWriter<Task> taskCsvImportItemWriter() {
        return tasks -> {
            for (Task task: tasks) {
                taskRepository.save(task);
            }
        };
    }

    @Bean
    Step taskCsvImportStep(
        ItemReader<Task> taskCsvImportItemReader,
        ItemProcessor<Task, Task> taskCsvImportLoggingProcessor,
        ItemWriter<Task> taskCsvImportItemWriter,
        StepBuilderFactory stepBuilderFactory
    ) {
        return stepBuilderFactory.get("taskCsvImportStep")
            .<Task, Task>chunk(1)
            .reader(taskCsvImportItemReader)
            .processor(taskCsvImportLoggingProcessor)
            .writer(taskCsvImportItemWriter)
            .build();
    }

    @Bean
    Job taskCsvImportJob(
        JobBuilderFactory jobBuilderFactory,
        @Qualifier("taskCsvImportStep") Step taskCsvImportStep
    ) {
        return jobBuilderFactory.get("taskCsvImportJob")
            .incrementer(new RunIdIncrementer())
            .flow(taskCsvImportStep)
            .end()
            .build();
    }

    *//* Helper Functions *//*

    private Set<Label> getLabelsByNames(String[] names) {

        return Arrays.stream(names)
            .filter(name -> !name.trim().isEmpty())
            .map(name -> {
                Label label = new Label();
                label.setName(name);
                return labelRepository.findOne(Example.of(label)).orElse(label);
            })
            .collect(Collectors.toSet());
    }*/
}

package ch.bloodpressure;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class BloodpressureApplicationIT extends AbstractBloodpressureIT {

    @Test
    void shouldContainPatientServiceBean() {
        //Given
        var patientServiceName = "patientServiceImpl";

        //when
        var beanNames = Arrays.stream(context.getBeanDefinitionNames()).toList();
        var patientService = context.getBean(patientServiceName);

        //Then
        assertThat(context).isNotNull();
        assertThat(beanNames).isNotEmpty();
        assertThat(patientService).isNotNull();
    }
}

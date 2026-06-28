package com.piano.management.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piano.management.entity.*;
import com.piano.management.exception.GlobalExceptionHandler;
import com.piano.management.mapper.*;
import com.piano.management.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SystemBusinessFlowMockMvcTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockMvc mockMvc;

    private ActivityService activityService;
    private ActivityRegistrationService registrationService;
    private SysUserService sysUserService;
    private BannerService bannerService;
    private CategoryService categoryService;
    private CertificateService certificateService;
    private ClassRecordService classRecordService;
    private ClassroomService classroomService;
    private CourseBookingService bookingService;
    private CourseService courseService;
    private OperationLogService operationLogService;
    private OrderInfoService orderService;
    private ProductService productService;
    private StockRecordService stockRecordService;
    private StudentCoursePackageService packageService;
    private TeacherQualificationService qualificationService;
    private TeacherScheduleSlotService slotService;
    private PackageAvailabilityService packageAvailabilityService;

    private ActivityRegistrationMapper registrationMapper;
    private CertificateMapper certificateMapper;
    private ClassRecordMapper classRecordMapper;
    private ConsultMessageMapper consultMessageMapper;
    private CourseBookingMapper bookingMapper;
    private OrderInfoMapper orderInfoMapper;
    private StudentCoursePackageMapper packageMapper;

    @BeforeEach
    void setUp() {
        activityService = mock(ActivityService.class);
        registrationService = mock(ActivityRegistrationService.class);
        sysUserService = mock(SysUserService.class);
        bannerService = mock(BannerService.class);
        categoryService = mock(CategoryService.class);
        certificateService = mock(CertificateService.class);
        classRecordService = mock(ClassRecordService.class);
        classroomService = mock(ClassroomService.class);
        bookingService = mock(CourseBookingService.class);
        courseService = mock(CourseService.class);
        operationLogService = mock(OperationLogService.class);
        orderService = mock(OrderInfoService.class);
        productService = mock(ProductService.class);
        stockRecordService = mock(StockRecordService.class);
        packageService = mock(StudentCoursePackageService.class);
        qualificationService = mock(TeacherQualificationService.class);
        slotService = mock(TeacherScheduleSlotService.class);
        packageAvailabilityService = mock(PackageAvailabilityService.class);

        registrationMapper = mock(ActivityRegistrationMapper.class);
        certificateMapper = mock(CertificateMapper.class);
        classRecordMapper = mock(ClassRecordMapper.class);
        consultMessageMapper = mock(ConsultMessageMapper.class);
        bookingMapper = mock(CourseBookingMapper.class);
        orderInfoMapper = mock(OrderInfoMapper.class);
        packageMapper = mock(StudentCoursePackageMapper.class);

        ActivityController activityController = new ActivityController();
        inject(activityController, "activityService", activityService);

        ActivityRegistrationController activityRegistrationController = new ActivityRegistrationController();
        inject(activityRegistrationController, "registrationService", registrationService);
        inject(activityRegistrationController, "registrationMapper", registrationMapper);
        inject(activityRegistrationController, "activityService", activityService);
        inject(activityRegistrationController, "orderService", orderService);

        AuthController authController = new AuthController();
        inject(authController, "sysUserService", sysUserService);

        BannerController bannerController = new BannerController();
        inject(bannerController, "bannerService", bannerService);

        CategoryController categoryController = new CategoryController();
        inject(categoryController, "categoryService", categoryService);

        CertificateController certificateController = new CertificateController();
        inject(certificateController, "certificateService", certificateService);
        inject(certificateController, "certificateMapper", certificateMapper);

        ClassRecordController classRecordController = new ClassRecordController();
        inject(classRecordController, "classRecordService", classRecordService);
        inject(classRecordController, "classRecordMapper", classRecordMapper);
        inject(classRecordController, "bookingService", bookingService);
        inject(classRecordController, "packageService", packageService);

        ClassroomController classroomController = new ClassroomController();
        inject(classroomController, "classroomService", classroomService);

        ConsultController consultController = new ConsultController();
        inject(consultController, "consultMessageMapper", consultMessageMapper);

        CourseBookingController courseBookingController = new CourseBookingController();
        inject(courseBookingController, "bookingService", bookingService);
        inject(courseBookingController, "bookingMapper", bookingMapper);
        inject(courseBookingController, "packageService", packageService);
        inject(courseBookingController, "packageAvailabilityService", packageAvailabilityService);
        inject(courseBookingController, "slotService", slotService);
        inject(courseBookingController, "courseService", courseService);
        inject(courseBookingController, "classroomService", classroomService);
        inject(courseBookingController, "sysUserService", sysUserService);
        inject(courseBookingController, "classRecordService", classRecordService);

        CourseController courseController = new CourseController();
        inject(courseController, "courseService", courseService);
        inject(courseController, "categoryService", categoryService);
        inject(courseController, "sysUserService", sysUserService);

        FileUploadController fileUploadController = new FileUploadController();
        String uploadPath = new File(System.getProperty("java.io.tmpdir"), "piano-upload-test").getAbsolutePath();
        inject(fileUploadController, "uploadPath", uploadPath);

        OperationLogController operationLogController = new OperationLogController();
        inject(operationLogController, "logService", operationLogService);

        OrderInfoController orderInfoController = new OrderInfoController();
        inject(orderInfoController, "orderService", orderService);
        inject(orderInfoController, "orderMapper", orderInfoMapper);
        inject(orderInfoController, "productService", productService);
        inject(orderInfoController, "courseService", courseService);
        inject(orderInfoController, "packageService", packageService);
        inject(orderInfoController, "registrationService", registrationService);

        ProductController productController = new ProductController();
        inject(productController, "productService", productService);
        inject(productController, "categoryService", categoryService);

        StudentCoursePackageController studentCoursePackageController = new StudentCoursePackageController();
        inject(studentCoursePackageController, "packageService", packageService);
        inject(studentCoursePackageController, "packageMapper", packageMapper);
        inject(studentCoursePackageController, "packageAvailabilityService", packageAvailabilityService);

        StatisticsController statisticsController = new StatisticsController();
        inject(statisticsController, "orderInfoMapper", orderInfoMapper);
        inject(statisticsController, "orderInfoService", orderService);
        inject(statisticsController, "activityRegistrationMapper", registrationMapper);
        inject(statisticsController, "courseBookingMapper", bookingMapper);
        inject(statisticsController, "courseBookingService", bookingService);
        inject(statisticsController, "sysUserService", sysUserService);
        inject(statisticsController, "courseService", courseService);

        StockRecordController stockRecordController = new StockRecordController();
        inject(stockRecordController, "stockRecordService", stockRecordService);
        inject(stockRecordController, "productService", productService);

        SysUserController sysUserController = new SysUserController();
        inject(sysUserController, "sysUserService", sysUserService);

        TeacherQualificationController qualificationController = new TeacherQualificationController();
        inject(qualificationController, "qualificationService", qualificationService);

        TeacherScheduleSlotController teacherScheduleSlotController = new TeacherScheduleSlotController();
        inject(teacherScheduleSlotController, "slotService", slotService);
        inject(teacherScheduleSlotController, "sysUserService", sysUserService);
        inject(teacherScheduleSlotController, "classroomService", classroomService);
        inject(teacherScheduleSlotController, "bookingService", bookingService);

        mockMvc = MockMvcBuilders.standaloneSetup(
                        activityController, activityRegistrationController, authController, bannerController,
                        categoryController, certificateController, classRecordController, classroomController,
                        consultController, courseBookingController, courseController, fileUploadController,
                        operationLogController, orderInfoController, productController, studentCoursePackageController, statisticsController,
                        stockRecordController, sysUserController, qualificationController, teacherScheduleSlotController
                )
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        when(registrationMapper.selectPageWithNames(any())).thenReturn(new ArrayList<>());
        when(certificateMapper.selectPageWithNames(any())).thenReturn(new ArrayList<>());
        when(classRecordMapper.selectPageWithNames(any())).thenReturn(new ArrayList<>());
        when(bookingMapper.selectPageWithNames(any())).thenReturn(new ArrayList<>());
        when(orderInfoMapper.selectPageWithNames(any())).thenReturn(new ArrayList<>());
        when(packageMapper.selectPageWithNames(any())).thenReturn(new ArrayList<>());
        when(consultMessageMapper.selectList(any())).thenReturn(new ArrayList<>());

        when(bookingMapper.selectBookingCountByStatus()).thenReturn(new ArrayList<>());
        when(orderInfoMapper.selectTopProductIncome()).thenReturn(new ArrayList<>());
        when(orderInfoMapper.selectMonthlyIncome(anyString())).thenReturn(new ArrayList<>());
        when(orderInfoMapper.selectIncomeByType()).thenReturn(new ArrayList<>());
        when(registrationMapper.selectParticipationByActivity()).thenReturn(new ArrayList<>());
        when(registrationMapper.selectCount(any())).thenReturn(0L);
        ActivityRegistration dbReg = new ActivityRegistration();
        dbReg.setId(1L);
        dbReg.setStatus(1);
        dbReg.setFeePaid(1);
        when(registrationService.getById(anyLong())).thenReturn(dbReg);
        when(orderInfoMapper.selectPaidOverview()).thenReturn(new HashMap<>());
        when(orderService.list(any())).thenReturn(new ArrayList<>());
        when(bookingService.count(any())).thenReturn(0L);
        CourseBooking dbBooking = new CourseBooking();
        dbBooking.setId(1L);
        dbBooking.setTeacherId(200L);
        dbBooking.setStudentId(101L);
        dbBooking.setCourseId(1L);
        dbBooking.setStudentPackageId(1L);
        dbBooking.setHoursCost(1);
        dbBooking.setStatus(0);
        dbBooking.setClassTime(java.time.LocalDateTime.of(2099, 1, 1, 10, 0, 0));
        when(bookingService.getById(anyLong())).thenReturn(dbBooking);
        StudentCoursePackage dbPackage = new StudentCoursePackage();
        dbPackage.setId(1L);
        dbPackage.setStudentId(101L);
        dbPackage.setCourseId(1L);
        dbPackage.setTeacherId(200L);
        dbPackage.setRemainingHours(10);
        dbPackage.setTotalHours(10);
        dbPackage.setStatus(1);
        when(packageService.getById(anyLong())).thenReturn(dbPackage);
        when(packageService.getOne(any(), anyBoolean())).thenReturn(dbPackage);
        when(packageService.list(any())).thenReturn(Collections.singletonList(dbPackage));
        when(packageAvailabilityService.calculateAvailableHours(any(StudentCoursePackage.class), any()))
                .thenAnswer(invocation -> {
                    StudentCoursePackage pkg = invocation.getArgument(0);
                    return pkg == null || pkg.getRemainingHours() == null ? 0 : pkg.getRemainingHours();
                });
        when(packageAvailabilityService.calculateAvailableHours(anyCollection()))
                .thenAnswer(invocation -> {
                    Collection<StudentCoursePackage> pkgs = invocation.getArgument(0);
                    Map<Long, Integer> result = new HashMap<>();
                    if (pkgs != null) {
                        for (StudentCoursePackage pkg : pkgs) {
                            if (pkg != null && pkg.getId() != null) {
                                result.put(pkg.getId(), pkg.getRemainingHours() == null ? 0 : pkg.getRemainingHours());
                            }
                        }
                    }
                    return result;
                });
        TeacherScheduleSlot slot = new TeacherScheduleSlot();
        slot.setId(1L);
        slot.setTeacherId(200L);
        slot.setSlotTime(java.time.LocalDateTime.of(2099, 1, 1, 10, 0, 0));
        slot.setDuration(60);
        slot.setStatus(1);
        when(slotService.getOne(any(), anyBoolean())).thenReturn(slot);
        when(sysUserService.count(any())).thenReturn(0L);

        Course baseCourse = new Course();
        baseCourse.setId(1L);
        baseCourse.setName("Piano");
        baseCourse.setTeacherId(200L);
        baseCourse.setDuration(60);
        baseCourse.setPrice(new BigDecimal("200.00"));
        when(courseService.getById(1L)).thenReturn(baseCourse);

        when(activityService.page(any(Page.class), any())).thenReturn(emptyPage());
        when(activityService.getById(anyLong())).thenReturn(new Activity());
        when(bannerService.page(any(Page.class), any())).thenReturn(emptyPage());
        when(categoryService.page(any(Page.class), any())).thenReturn(emptyPage());
        when(classroomService.page(any(Page.class), any())).thenReturn(emptyPage());
        when(courseService.page(any(Page.class), any())).thenReturn(emptyPage());
        when(operationLogService.page(any(Page.class), any())).thenReturn(emptyPage());
        when(productService.page(any(Page.class), any())).thenReturn(emptyPage());
        when(stockRecordService.page(any(Page.class), any())).thenReturn(emptyPage());
        when(qualificationService.page(any(Page.class), any())).thenReturn(emptyPage());
        when(sysUserService.page(any(Page.class), any())).thenReturn(emptyPage());

        when(activityService.list()).thenReturn(new ArrayList<>());
        when(bannerService.list()).thenReturn(new ArrayList<>());
        when(classroomService.list()).thenReturn(new ArrayList<>());
        when(courseService.list()).thenReturn(new ArrayList<>());
        when(courseService.list(any())).thenReturn(Collections.singletonList(baseCourse));
        when(productService.list(any())).thenReturn(new ArrayList<>());
        when(categoryService.list()).thenReturn(Collections.singletonList(category(1L, "Piano")));
        when(sysUserService.listByIds(anyCollection())).thenReturn(Collections.singletonList(user(200L, "TEACHER")));
        when(sysUserService.list(any())).thenReturn(Collections.singletonList(user(200L, "TEACHER")));
    }

    @Test
    void authFlowShouldPass() throws Exception {
        SysUser loginUser = user(1L, "ADMIN");
        loginUser.setUsername("admin");
        loginUser.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        loginUser.setStatus(1);

        SysUser infoUser = user(1L, "ADMIN");
        SysUser passwordUser = user(1L, "ADMIN");
        passwordUser.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        SysUser updatedUser = user(1L, "ADMIN");
        updatedUser.setName("new-name");

        when(sysUserService.getOne(any())).thenReturn(loginUser, null);
        when(sysUserService.getById(1L)).thenReturn(infoUser, passwordUser, updatedUser);

        MvcResult login = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map("username", "admin", "password", "123456"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();
        MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);
        assertNotNull(session);

        mockMvc.perform(get("/api/auth/info").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(put("/api/auth/password").session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map("oldPassword", "123456", "newPassword", "654321"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(put("/api/auth/profile").session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map("name", "new-name", "phone", "13300000000"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(post("/api/auth/logout").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map("username", "student_a", "password", "123456", "role", "STUDENT"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void adminBusinessFlowShouldPass() throws Exception {
        MockHttpSession session = session("ADMIN", 1L);

        when(sysUserService.getById(1L)).thenReturn(user(1L, "ADMIN"));
        when(categoryService.getById(1L)).thenReturn(category(1L, "Piano"));
        when(classroomService.getById(1L)).thenReturn(new Classroom());
        when(activityService.getById(1L)).thenReturn(new Activity());
        when(bannerService.getById(1L)).thenReturn(new Banner());
        when(productService.getById(1L)).thenReturn(product(1L, 10));
        OrderInfo dbOrder = new OrderInfo();
        dbOrder.setId(1L);
        dbOrder.setStatus(0);
        dbOrder.setOrderType("PRODUCT");
        dbOrder.setProductId(1L);
        dbOrder.setQuantity(1);
        when(orderService.getById(1L)).thenReturn(dbOrder);
        when(courseService.getById(1L)).thenReturn(new Course());

        ok(get("/api/user").session(session));
        ok(get("/api/user/teachers").session(session));
        ok(get("/api/user/1").session(session));
        ok(post("/api/user").session(session), map("username", "u1", "name", "U1", "role", "TEACHER"));
        ok(put("/api/user").session(session), map("id", 1, "name", "U1-2"));
        ok(put("/api/user/status/1?status=1").session(session));
        ok(put("/api/user/resetPwd/1").session(session));
        ok(delete("/api/user/1").session(session));

        ok(get("/api/category").session(session));
        ok(get("/api/category/all").session(session));
        ok(get("/api/category/1").session(session));
        ok(post("/api/category").session(session), map("name", "Category-A"));
        ok(put("/api/category").session(session), map("id", 1, "name", "Category-B"));
        ok(delete("/api/category/1").session(session));

        ok(get("/api/classroom").session(session));
        ok(get("/api/classroom/all").session(session));
        ok(get("/api/classroom/1").session(session));
        ok(post("/api/classroom").session(session), map("name", "A101"));
        ok(put("/api/classroom").session(session), map("id", 1, "name", "A102"));
        ok(delete("/api/classroom/1").session(session));

        ok(get("/api/activity").session(session));
        ok(get("/api/activity/all").session(session));
        ok(get("/api/activity/1").session(session));
        ok(post("/api/activity").session(session), map("title", "Competition"));
        ok(put("/api/activity").session(session), map("id", 1, "title", "Competition 2"));
        ok(delete("/api/activity/1").session(session));

        ok(get("/api/banner").session(session));
        ok(get("/api/banner/all").session(session));
        ok(get("/api/banner/1").session(session));
        ok(post("/api/banner").session(session), map("title", "Banner"));
        ok(put("/api/banner").session(session), map("id", 1, "title", "Banner 2"));
        ok(delete("/api/banner/1").session(session));

        ok(get("/api/product").session(session));
        ok(get("/api/product/warning").session(session));
        ok(get("/api/product/1").session(session));
        ok(post("/api/product").session(session), map("name", "Keyboard", "categoryId", 1, "stock", 10));
        ok(put("/api/product").session(session), map("id", 1, "name", "Keyboard 2"));
        ok(delete("/api/product/1").session(session));

        ok(get("/api/studentPackage").session(session));
        ok(get("/api/studentPackage/1").session(session));
        ok(post("/api/studentPackage").session(session), map(
                "studentId", 101, "courseId", 1, "teacherId", 200, "name", "Piano Pack",
                "totalHours", 10, "remainingHours", 10, "status", 1));
        ok(put("/api/studentPackage").session(session), map("id", 1, "remainingHours", 9));
        ok(delete("/api/studentPackage/1").session(session));

        ok(get("/api/course").session(session));
        ok(get("/api/course/all").session(session));
        ok(get("/api/course/1").session(session));
        ok(post("/api/course").session(session), map("name", "Piano L1", "teacherId", 200, "categoryId", 1));
        ok(put("/api/course").session(session), map("id", 1, "name", "Piano L2"));
        ok(delete("/api/course/1").session(session));

        when(productService.getById(1L)).thenReturn(product(1L, 10));
        ok(get("/api/stockRecord").session(session));
        ok(post("/api/stockRecord").session(session), map("productId", 1, "type", "IN", "quantity", 5));

        ok(get("/api/order").session(session));
        ok(post("/api/order").session(session), map(
                "studentId", 101, "orderType", "PRODUCT", "productId", 1, "quantity", 1,
                "actualAmount", new BigDecimal("1999.00")));
        ok(put("/api/order").session(session), map("id", 1, "status", 1));
        ok(put("/api/order/refund/1").session(session));
        ok(delete("/api/order/1").session(session));

        ok(get("/api/log").session(session));
        ok(get("/api/statistics/overview").session(session));
        ok(get("/api/statistics/bookingByStatus").session(session));
        ok(get("/api/statistics/topProducts").session(session));
        ok(get("/api/statistics/monthlyIncome").session(session));
        ok(get("/api/statistics/incomeByType").session(session));
        ok(get("/api/statistics/activityParticipation").session(session));
        ok(get("/api/statistics/teacherIncome?teacherId=200&period=month").session(session));
    }

    @Test
    void teachingAndStudentFlowShouldPass() throws Exception {
        MockHttpSession student = session("STUDENT", 101L);
        MockHttpSession teacher = session("TEACHER", 200L);
        MockHttpSession admin = session("ADMIN", 1L);

        ok(get("/api/booking").session(student));
        ok(post("/api/booking").session(student), map(
                "studentId", 101, "teacherId", 200, "courseId", 1, "status", 0, "classTime", "2099-01-01 10:00:00"));
        ok(put("/api/booking").session(teacher), map("id", 1, "status", 1));
        ok(delete("/api/booking/1").session(admin));

        ok(get("/api/classRecord").session(teacher));
        ok(post("/api/classRecord").session(teacher), map("bookingId", 1, "studentId", 101, "teacherId", 200));
        ok(put("/api/classRecord").session(teacher), map("id", 1, "remark", "done"));
        ok(put("/api/classRecord/evaluate").session(student), map("id", 1, "courseScore", 5, "teacherScore", 5, "evaluation", "good"));
        ok(delete("/api/classRecord/evaluation/1").session(teacher));
        ok(delete("/api/classRecord/1").session(admin));

        ok(get("/api/certificate").session(admin));
        ok(post("/api/certificate").session(admin), map("studentId", 101, "type", "Grade-10"));
        ok(put("/api/certificate").session(admin), map("id", 1, "type", "Grade-10-Updated"));
        ok(delete("/api/certificate/1").session(admin));

        ok(get("/api/registration").session(admin));
        ok(post("/api/registration").session(student), map("activityId", 1, "studentId", 101, "teacherId", 200));
        ok(put("/api/registration").session(admin), map("id", 1, "status", 1));
        ok(put("/api/registration/approve/1?status=1").session(admin));
        ok(put("/api/registration/result/1").session(admin), map("passed", 1, "rank", 2));
        ok(delete("/api/registration/1").session(admin));

        ok(get("/api/qualification?teacherId=200").session(teacher));
        ok(post("/api/qualification").session(teacher), map("teacherId", 200, "name", "Cert-A"));
        ok(put("/api/qualification").session(teacher), map("id", 1, "name", "Cert-B"));
        ok(delete("/api/qualification/1").session(teacher));

        ok(post("/api/consult/message").session(student), map(
                "courseId", 1, "teacherId", 200, "studentId", 101, "content", "hello teacher"));
        ok(get("/api/consult/messages?courseId=1&teacherId=200&studentId=101").session(student));
        ok(post("/api/consult/message").session(teacher), map(
                "courseId", 1, "teacherId", 200, "studentId", 101, "content", "hello student"));
        ok(get("/api/consult/messages?courseId=1&teacherId=200&studentId=101").session(teacher));
    }

    @Test
    void negativeAndDataMutationCasesShouldPass() throws Exception {
        MockHttpSession student = session("STUDENT", 101L);

        mockMvc.perform(multipart("/api/upload").file(new MockMultipartFile(
                                "file", "ok.jpg", "image/jpeg", "abc".getBytes())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", startsWith("/uploads/")));

        mockMvc.perform(multipart("/api/upload").file(new MockMultipartFile(
                                "file", "badfile", "text/plain", "abc".getBytes())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));

        Product lowStock = product(2L, 1);
        when(productService.getById(2L)).thenReturn(lowStock);
        mockMvc.perform(post("/api/stockRecord")
                        .session(session("ADMIN", 1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map("productId", 2, "type", "OUT", "quantity", 5))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));

        mockMvc.perform(get("/api/consult/messages")
                        .session(student)
                        .param("courseId", "1")
                        .param("teacherId", "200")
                        .param("studentId", "999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));

        when(productService.getById(3L)).thenReturn(product(3L, 7));
        mockMvc.perform(post("/api/stockRecord")
                        .session(session("ADMIN", 1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map("productId", 3, "type", "IN", "quantity", 2))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<OrderInfo> orderCaptor = ArgumentCaptor.forClass(OrderInfo.class);
        mockMvc.perform(post("/api/order")
                        .session(session("ADMIN", 1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map("studentId", 101, "orderType", "PRODUCT"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        verify(orderService, atLeastOnce()).save(orderCaptor.capture());
        OrderInfo savedOrder = orderCaptor.getValue();
        assertNotNull(savedOrder.getOrderNo());
        assertEquals(20, savedOrder.getOrderNo().length());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<CourseBooking> bookingCaptor = ArgumentCaptor.forClass(CourseBooking.class);
        mockMvc.perform(post("/api/booking")
                        .session(session("STUDENT", 101L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map("studentId", 101, "teacherId", 200, "courseId", 1, "classTime", "2099-01-01 10:00:00"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        verify(bookingService, atLeastOnce()).save(bookingCaptor.capture());
        CourseBooking savedBooking = bookingCaptor.getValue();
        assertNotNull(savedBooking.getBookingTime());
        assertEquals(0, savedBooking.getStatus());

        ActivityRegistration unpaidReg = new ActivityRegistration();
        unpaidReg.setId(2L);
        unpaidReg.setStatus(1);
        unpaidReg.setFeePaid(0);
        when(registrationService.getById(2L)).thenReturn(unpaidReg);
        mockMvc.perform(put("/api/registration/result/2")
                        .session(session("ADMIN", 1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map("passed", 1, "rank", 1))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("报名未缴费，不能录入成绩"));

        mockMvc.perform(put("/api/registration")
                        .session(session("ADMIN", 1L))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map("id", 2, "passed", 1, "rank", 1))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("报名未缴费，不能录入成绩"));
    }

    @Test
    void issue517FixesShouldBeEnforcedByBackend() throws Exception {
        MockHttpSession admin = session("ADMIN", 1L);
        MockHttpSession student = session("STUDENT", 101L);
        MockHttpSession teacher = session("TEACHER", 200L);

        mockMvc.perform(post("/api/activity")
                        .session(admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map(
                                "name", "Bad Activity",
                                "startTime", "2099-05-02 10:00:00",
                                "endTime", "2099-05-01 10:00:00"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("结束时间不能早于开始时间"));
        verify(activityService, never()).save(argThat(activity ->
                activity != null && "Bad Activity".equals(activity.getName())));

        CourseBooking expiredPending = new CourseBooking();
        expiredPending.setId(88L);
        expiredPending.setStatus(0);
        expiredPending.setClassTime(java.time.LocalDateTime.now().minusDays(1));
        when(bookingService.list(any())).thenReturn(Collections.singletonList(expiredPending));
        when(bookingMapper.selectPageWithNames(any())).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/api/booking").session(admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        verify(bookingService, atLeastOnce()).updateById(argThat(booking ->
                booking != null && Objects.equals(booking.getId(), 88L) && Objects.equals(booking.getStatus(), 4)));

        CourseBooking pastPending = new CourseBooking();
        pastPending.setId(89L);
        pastPending.setStatus(0);
        pastPending.setStudentId(101L);
        pastPending.setTeacherId(200L);
        pastPending.setCourseId(1L);
        pastPending.setStudentPackageId(1L);
        pastPending.setClassTime(java.time.LocalDateTime.now().minusHours(1));
        pastPending.setDuration(60);
        when(bookingService.getById(89L)).thenReturn(pastPending);
        when(bookingService.list(any())).thenReturn(new ArrayList<>());
        mockMvc.perform(put("/api/booking")
                        .session(teacher)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map("id", 89, "status", 1))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("上课时间不能早于当前时间"));

        Classroom disabledClassroom = new Classroom();
        disabledClassroom.setId(9L);
        disabledClassroom.setStatus(0);
        when(classroomService.getById(9L)).thenReturn(disabledClassroom);
        mockMvc.perform(post("/api/teacherSlot")
                        .session(teacher)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map(
                                "teacherId", 200,
                                "classroomId", 9,
                                "slotTime", "2099-01-02 10:00:00",
                                "duration", 60,
                                "status", 1))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("教室已停用，不能用于预约"));

        TeacherScheduleSlot disabledClassroomSlot = new TeacherScheduleSlot();
        disabledClassroomSlot.setId(9L);
        disabledClassroomSlot.setTeacherId(200L);
        disabledClassroomSlot.setClassroomId(9L);
        disabledClassroomSlot.setSlotTime(java.time.LocalDateTime.of(2099, 1, 3, 10, 0, 0));
        disabledClassroomSlot.setDuration(60);
        disabledClassroomSlot.setStatus(1);
        when(slotService.getOne(any(), anyBoolean())).thenReturn(disabledClassroomSlot);
        mockMvc.perform(post("/api/booking")
                        .session(student)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map(
                                "studentId", 101,
                                "teacherId", 200,
                                "courseId", 1,
                                "classTime", "2099-01-03 10:00:00"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("教室已停用，不能用于预约"));
    }

    @Test
    void changingSlotClassroomShouldSyncActiveBookings() throws Exception {
        MockHttpSession teacher = session("TEACHER", 200L);

        Classroom activeClassroom = new Classroom();
        activeClassroom.setId(2L);
        activeClassroom.setStatus(1);
        when(classroomService.getById(2L)).thenReturn(activeClassroom);

        TeacherScheduleSlot dbSlot = new TeacherScheduleSlot();
        dbSlot.setId(20L);
        dbSlot.setTeacherId(200L);
        dbSlot.setClassroomId(1L);
        dbSlot.setSlotTime(java.time.LocalDateTime.of(2099, 1, 4, 10, 0, 0));
        dbSlot.setDuration(60);
        dbSlot.setStatus(1);
        when(slotService.getById(20L)).thenReturn(dbSlot);
        when(slotService.count(any())).thenReturn(0L);

        CourseBooking activeBooking = new CourseBooking();
        activeBooking.setId(30L);
        activeBooking.setTeacherId(200L);
        activeBooking.setClassTime(java.time.LocalDateTime.of(2099, 1, 4, 10, 0, 0));
        activeBooking.setDuration(60);
        activeBooking.setStatus(1);
        when(bookingService.list(any())).thenReturn(Collections.singletonList(activeBooking));

        mockMvc.perform(put("/api/teacherSlot")
                        .session(teacher)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(map(
                                "id", 20,
                                "teacherId", 200,
                                "classroomId", 2,
                                "slotTime", "2099-01-04 10:00:00",
                                "duration", 60,
                                "status", 1))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(slotService, atLeastOnce()).updateById(argThat(slot ->
                slot != null && Objects.equals(slot.getId(), 20L) && Objects.equals(slot.getClassroomId(), 2L)));
        verify(bookingService, atLeastOnce()).updateById(argThat(booking ->
                booking != null && Objects.equals(booking.getId(), 30L) && Objects.equals(booking.getClassroomId(), 2L)));
    }

    private MockHttpSession session(String role, Long id) {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loginUser", user(id, role));
        return session;
    }

    private SysUser user(Long id, String role) {
        SysUser u = new SysUser();
        u.setId(id);
        u.setRole(role);
        u.setName(role + "-" + id);
        u.setStatus(1);
        return u;
    }

    private Category category(Long id, String name) {
        Category c = new Category();
        c.setId(id);
        c.setName(name);
        return c;
    }

    private Product product(Long id, int stock) {
        Product p = new Product();
        p.setId(id);
        p.setStock(stock);
        return p;
    }

    @SuppressWarnings("rawtypes")
    private Page emptyPage() {
        Page p = new Page(1, 10);
        p.setRecords(new ArrayList<>());
        p.setTotal(0);
        return p;
    }

    private void ok(MockHttpServletRequestBuilder builder) throws Exception {
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    private void ok(MockHttpServletRequestBuilder builder, Map<String, Object> body) throws Exception {
        mockMvc.perform(builder.contentType(MediaType.APPLICATION_JSON).content(json(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    private Map<String, Object> map(Object... kv) {
        if (kv.length % 2 != 0) {
            throw new IllegalArgumentException("key/value length must be even");
        }
        Map<String, Object> result = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            result.put(String.valueOf(kv[i]), kv[i + 1]);
        }
        return result;
    }

    private void inject(Object target, String field, Object value) {
        ReflectionTestUtils.setField(target, field, value);
    }
}

<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryForm"
      :inline="true"
      v-show="showSearch"
      label-width="68px"
    >
      <el-form-item label="顺序" prop="ordernum">
        <el-input
          v-model="queryParams.ordernum"
          placeholder="请输入顺序"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建时间" prop="creationtime">
        <el-date-picker
          clearable
          size="small"
          v-model="queryParams.creationtime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="选择创建时间"
        >
        </el-date-picker>
      </el-form-item>
      <el-form-item label="创建人" prop="creatoruserid">
        <el-input
          v-model="queryParams.creatoruserid"
          placeholder="请输入创建人"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="最后修改时间" prop="lastmodificationtime">
        <el-date-picker
          clearable
          size="small"
          v-model="queryParams.lastmodificationtime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="选择最后修改时间"
        >
        </el-date-picker>
      </el-form-item>
      <el-form-item label="最后修改人" prop="lastmodifieruserid">
        <el-input
          v-model="queryParams.lastmodifieruserid"
          placeholder="请输入最后修改人"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否删除" prop="isdeleted">
        <el-input
          v-model="queryParams.isdeleted"
          placeholder="请输入是否删除"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="删除人" prop="deleteruserid">
        <el-input
          v-model="queryParams.deleteruserid"
          placeholder="请输入删除人"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="删除时间" prop="deletiontime">
        <el-date-picker
          clearable
          size="small"
          v-model="queryParams.deletiontime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="选择删除时间"
        >
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          icon="el-icon-search"
          size="mini"
          @click="handleQuery"
          >搜索</el-button
        >
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery"
          >重置</el-button
        >
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['cms:sites:add']"
          >新增</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['cms:sites:edit']"
          >修改</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['cms:sites:remove']"
          >删除</el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['cms:sites:export']"
          >导出</el-button
        >
      </el-col>
      <right-toolbar
        :showSearch.sync="showSearch"
        @queryTable="getList"
      ></right-toolbar>
    </el-row>

    <el-table
      v-loading="loading"
      :data="sitesList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="id" align="center" prop="id" />
      <el-table-column label="站点目录" align="center" prop="sitedir" />
      <el-table-column label="站点名称" align="center" prop="sitename" />
      <el-table-column label="站点类型" align="center" prop="sitetype" />
      <el-table-column label="站点图片" align="center" prop="imageurl" />
      <el-table-column label="关键字" align="center" prop="keywords" />
      <el-table-column label="站点描述" align="center" prop="description" />
      <el-table-column label="关联表名" align="center" prop="tablename" />
      <el-table-column label="是否根目录" align="center" prop="root" />
      <el-table-column label="上级站点" align="center" prop="parentid" />
      <el-table-column label="顺序" align="center" prop="ordernum" />
      <el-table-column
        label="创建时间"
        align="center"
        prop="creationtime"
        width="180"
      >
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.creationtime, "{y}-{m}-{d}") }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建人" align="center" prop="creatoruserid" />

      <el-table-column
        label="操作"
        align="center"
        class-name="small-padding fixed-width"
      >
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['cms:sites:edit']"
            >修改</el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['cms:sites:remove']"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改站点对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="扩展内容" prop="extendvalues">
          <el-input
            v-model="form.extendvalues"
            type="textarea"
            placeholder="请输入内容"
          />
        </el-form-item>
        <el-form-item label="站点目录" prop="sitedir">
          <el-input
            v-model="form.sitedir"
            type="text"
            placeholder="请输入内容"
          />
        </el-form-item>
        <el-form-item label="站点名称" prop="sitename">
          <el-input
            v-model="form.sitename"
            type="text"
            placeholder="请输入内容"
          />
        </el-form-item>
        <el-form-item label="站点图片" prop="imageurl">
          <el-upload
            class="avatar-uploader"
            :headers="headers"
            action="http://localhost:8080/common/upload"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
          >
            <img v-if="form.imageurl" :src="form.imageurl" class="avatar" />
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-form-item>
        <el-form-item label="关键字" prop="keywords">
          <el-input
            v-model="form.keywords"
            type="text"
            placeholder="请输入内容"
          />
        </el-form-item>
        <el-form-item label="网站类型" prop="sitetype">
          <el-select v-model="form.sitetype" placeholder="请选择">
            <el-option
              v-for="item in siteType"
              :key="item.val"
              :label="item.name"
              :value="item.val"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="站点描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            placeholder="请输入内容"
          />
        </el-form-item>
        <el-form-item label="是否根目录" prop="root">
          <el-switch v-model="form.root" />
        </el-form-item>
        <el-form-item label="顺序" prop="ordernum">
          <el-input v-model="form.ordernum" placeholder="请输入顺序" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<style>
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>

<script>
import {
  listSites,
  getSites,
  delSites,
  addSites,
  updateSites,
} from "@/api/cms/sites";
import { getToken } from "@/utils/auth";

export default {
  name: "Sites",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      //文件
      fileList: [],
      // 站点表格数据
      sitesList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        sitedir: null,
        sitename: null,
        sitetype: null,
        imageurl: null,
        keywords: null,
        description: null,
        tablename: null,
        root: null,
        parentid: null,
        ordernum: null,
        creationtime: null,
        creatoruserid: null,
        lastmodificationtime: null,
        lastmodifieruserid: null,
        isdeleted: null,
        deleteruserid: null,
        deletiontime: null,
      },
      //站点类型
      siteType: [
        { name: "web", val: "web" },
        { name: "mobile", val: "mobile" },
        { name: "Wx", val: "Wx" },
      ],
      // 表单参数
      form: {
        imageurl:null
      },
      // 表单校验
      rules: {
        root: [
          { required: true, message: "是否根目录不能为空", trigger: "blur" },
        ],
        parentid: [
          { required: true, message: "上级站点不能为空", trigger: "blur" },
        ],
        ordernum: [
          { required: true, message: "顺序不能为空", trigger: "blur" },
        ],
        creationtime: [
          { required: true, message: "创建时间不能为空", trigger: "blur" },
        ],
        isdeleted: [
          { required: true, message: "是否删除不能为空", trigger: "blur" },
        ],
      },
    };
  },
  created() {
    this.getList();
  },
  computed: {
    headers() {
      return {
        Authorization: "Bearer " + getToken(),
      };
    },
  },
  methods: {
    handleAvatarSuccess(res, file) {
      console.log(file);
      console.log(res);
      console.log(URL.createObjectURL(file.raw));
      console.log(res.url);
      this.form.imageurl = URL.createObjectURL(file.raw);
      //this.form.imageurl = res.url;
    },
    beforeAvatarUpload(file) {
      const isJPG = file.type === "image/jpeg";
      const isLt2M = file.size / 1024 / 1024 < 10;

      if (!isJPG) {
        this.$message.error("上传头像图片只能是 JPG 格式!");
      }
      if (!isLt2M) {
        this.$message.error("上传头像图片大小不能超过 2MB!");
      }
      return isJPG && isLt2M;
    },
    handlePreview(file) {
      console.log(file);
    },
    handleRemove(file, fileList) {
      console.log(file, fileList);
    },
    handleExceed(files, fileList) {
      this.$message.warning(
        `当前限制选择 3 个文件，本次选择了 ${files.length} 个文件，共选择了 ${
          files.length + fileList.length
        } 个文件`
      );
    },
    beforeRemove(file, fileList) {
      return this.$confirm(`确定移除 ${file.name}？`);
    },
    /** 查询站点列表 */
    getList() {
      this.loading = true;
      listSites(this.queryParams).then((response) => {
        this.sitesList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        guid: null,
        extendvalues: null,
        sitedir: null,
        sitename: null,
        sitetype: null,
        imageurl: null,
        keywords: null,
        description: null,
        tablename: null,
        root: 0,
        parentid: 0,
        ordernum: 99,
        creationtime: null,
        creatoruserid: 0,
        lastmodificationtime: null,
        lastmodifieruserid: 0,
        isdeleted: 0,
        deleteruserid: 0,
        deletiontime: null,
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.id);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加站点";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids;
      getSites(id).then((response) => {
        this.form = response.data;
        this.open = true;
        this.title = "修改站点";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          if (this.form.id != null) {
            updateSites(this.form).then((response) => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            console.log(this.form);
            this.form.root = this.form.root ? 1 : 0;
            console.log(this.form);
            addSites(this.form).then((response) => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal
        .confirm('是否确认删除站点编号为"' + ids + '"的数据项？')
        .then(function () {
          return delSites(ids);
        })
        .then(() => {
          this.getList();
          this.$modal.msgSuccess("删除成功");
        })
        .catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download(
        "cms/sites/export",
        {
          ...this.queryParams,
        },
        `sites_${new Date().getTime()}.xlsx`
      );
    },
  },
};
</script>

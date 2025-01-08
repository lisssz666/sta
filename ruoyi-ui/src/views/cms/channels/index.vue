<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="所属站点" prop="siteid">
        <el-input
          v-model="queryParams.siteid"
          placeholder="请输入所属站点"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="父级ID" prop="parentid">
        <el-input
          v-model="queryParams.parentid"
          placeholder="请输入父级ID"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="父级数量" prop="parentscount">
        <el-input
          v-model="queryParams.parentscount"
          placeholder="请输入父级数量"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="子级数量" prop="childrencount">
        <el-input
          v-model="queryParams.childrencount"
          placeholder="请输入子级数量"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="频道模版" prop="channeltemplteid">
        <el-input
          v-model="queryParams.channeltemplteid"
          placeholder="请输入频道模版"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="内容模版" prop="contenttemplateid">
        <el-input
          v-model="queryParams.contenttemplateid"
          placeholder="请输入内容模版"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="排序" prop="ordernum">
        <el-input
          v-model="queryParams.ordernum"
          placeholder="请输入排序"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建时间" prop="creationtime">
        <el-date-picker clearable size="small"
          v-model="queryParams.creationtime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="选择创建时间">
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
        <el-date-picker clearable size="small"
          v-model="queryParams.lastmodificationtime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="选择最后修改时间">
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
        <el-date-picker clearable size="small"
          v-model="queryParams.deletiontime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="选择删除时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
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
          v-hasPermi="['cms:channels:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['cms:channels:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['cms:channels:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['cms:channels:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="channelsList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="id" align="center" prop="id" />
       
      <el-table-column label="扩展内容" align="center" prop="extendvalues" />
      <el-table-column label="频道名称" align="center" prop="channelname" />
      <el-table-column label="所属站点" align="center" prop="siteid" />
      <el-table-column label="内容插件ID" align="center" prop="contentmodelpluginid" />
      <el-table-column label="关联表名" align="center" prop="tablename" />
      <el-table-column label="父级ID" align="center" prop="parentid" />
      <el-table-column label="父级路径" align="center" prop="parentspath" />
      <el-table-column label="父级数量" align="center" prop="parentscount" />
      <el-table-column label="子级数量" align="center" prop="childrencount" />
      <el-table-column label="排序名称" align="center" prop="indexname" />
      <el-table-column label="分组" align="center" prop="groupnames" />
      <el-table-column label="图片" align="center" prop="imageurl" />
      <el-table-column label="内容描述" align="center" prop="content" />
      <el-table-column label="文件路径" align="center" prop="filepath" />
      <el-table-column label="频道路径路径规则" align="center" prop="channelfilepathrule" />
      <el-table-column label="内容文件路径规则" align="center" prop="contentfilepathrule" />
      <el-table-column label="外链" align="center" prop="linkurl" />
      <el-table-column label="外链类型" align="center" prop="linktype" />
      <el-table-column label="频道模版" align="center" prop="channeltemplteid" />
      <el-table-column label="内容模版" align="center" prop="contenttemplateid" />
      <el-table-column label="关键字" align="center" prop="keywords" />
      <el-table-column label="描述" align="center" prop="description" />
      <el-table-column label="排序" align="center" prop="ordernum" />
      <el-table-column label="创建时间" align="center" prop="creationtime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.creationtime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建人" align="center" prop="creatoruserid" />
      <el-table-column label="最后修改时间" align="center" prop="lastmodificationtime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.lastmodificationtime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="最后修改人" align="center" prop="lastmodifieruserid" />
      <el-table-column label="是否删除" align="center" prop="isdeleted" />
      <el-table-column label="删除人" align="center" prop="deleteruserid" />
      <el-table-column label="删除时间" align="center" prop="deletiontime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.deletiontime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['cms:channels:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['cms:channels:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改频道对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        
       
        <el-form-item label="频道名称" prop="channelname">
          <el-input v-model="form.channelname" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="所属站点" prop="siteid">
          <el-input v-model="form.siteid" placeholder="请输入所属站点" />
        </el-form-item>
        <el-form-item label="内容插件ID" prop="contentmodelpluginid">
          <el-input v-model="form.contentmodelpluginid" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="关联表名" prop="tablename">
          <el-input v-model="form.tablename" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="父级ID" prop="parentid">
          <el-input v-model="form.parentid" placeholder="请输入父级ID" />
        </el-form-item>
        <el-form-item label="父级路径" prop="parentspath">
          <el-input v-model="form.parentspath" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="父级数量" prop="parentscount">
          <el-input v-model="form.parentscount" placeholder="请输入父级数量" />
        </el-form-item>
        <el-form-item label="子级数量" prop="childrencount">
          <el-input v-model="form.childrencount" placeholder="请输入子级数量" />
        </el-form-item>
        <el-form-item label="排序名称" prop="indexname">
          <el-input v-model="form.indexname" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="分组" prop="groupnames">
          <el-input v-model="form.groupnames" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="图片" prop="imageurl">
          <el-input v-model="form.imageurl" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="内容描述">
          <editor v-model="form.content" :min-height="192"/>
        </el-form-item>
        <el-form-item label="文件路径" prop="filepath">
          <el-input v-model="form.filepath" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="频道路径路径规则" prop="channelfilepathrule">
          <el-input v-model="form.channelfilepathrule" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="内容文件路径规则" prop="contentfilepathrule">
          <el-input v-model="form.contentfilepathrule" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="外链" prop="linkurl">
          <el-input v-model="form.linkurl" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="频道模版" prop="channeltemplteid">
          <el-input v-model="form.channeltemplteid" placeholder="请输入频道模版" />
        </el-form-item>
        <el-form-item label="内容模版" prop="contenttemplateid">
          <el-input v-model="form.contenttemplateid" placeholder="请输入内容模版" />
        </el-form-item>
        <el-form-item label="关键字" prop="keywords">
          <el-input v-model="form.keywords" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="排序" prop="ordernum">
          <el-input v-model="form.ordernum" placeholder="请输入排序" />
        </el-form-item>
         <el-form-item label="扩展内容" prop="extendvalues">
          <el-input v-model="form.extendvalues" type="textarea" placeholder="请输入内容" />
        </el-form-item>
         
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listChannels, getChannels, delChannels, addChannels, updateChannels } from "@/api/cms/channels";

export default {
  name: "Channels",
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
      // 频道表格数据
      channelsList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        guid: null,
        extendvalues: null,
        channelname: null,
        siteid: null,
        contentmodelpluginid: null,
        tablename: null,
        parentid: null,
        parentspath: null,
        parentscount: null,
        childrencount: null,
        indexname: null,
        groupnames: null,
        imageurl: null,
        content: null,
        filepath: null,
        channelfilepathrule: null,
        contentfilepathrule: null,
        linkurl: null,
        linktype: null,
        channeltemplteid: null,
        contenttemplateid: null,
        keywords: null,
        description: null,
        ordernum: null,
        creationtime: null,
        creatoruserid: null,
        lastmodificationtime: null,
        lastmodifieruserid: null,
        isdeleted: null,
        deleteruserid: null,
        deletiontime: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        channelname: [
          { required: true, message: "频道名称不能为空", trigger: "blur" }
        ],
        siteid: [
          { required: true, message: "所属站点不能为空", trigger: "blur" }
        ],
        parentid: [
          { required: true, message: "父级ID不能为空", trigger: "blur" }
        ],
        parentscount: [
          { required: true, message: "父级数量不能为空", trigger: "blur" }
        ],
        childrencount: [
          { required: true, message: "子级数量不能为空", trigger: "blur" }
        ],
        channeltemplteid: [
          { required: true, message: "频道模版不能为空", trigger: "blur" }
        ],
        contenttemplateid: [
          { required: true, message: "内容模版不能为空", trigger: "blur" }
        ],
        ordernum: [
          { required: true, message: "排序不能为空", trigger: "blur" }
        ],
        creationtime: [
          { required: true, message: "创建时间不能为空", trigger: "blur" }
        ],
        isdeleted: [
          { required: true, message: "是否删除不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询频道列表 */
    getList() {
      this.loading = true;
      listChannels(this.queryParams).then(response => {
        this.channelsList = response.rows;
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
        channelname: null,
        siteid: null,
        contentmodelpluginid: null,
        tablename: null,
        parentid: null,
        parentspath: null,
        parentscount: null,
        childrencount: null,
        indexname: null,
        groupnames: null,
        imageurl: null,
        content: null,
        filepath: null,
        channelfilepathrule: null,
        contentfilepathrule: null,
        linkurl: null,
        linktype: null,
        channeltemplteid: null,
        contenttemplateid: null,
        keywords: null,
        description: null,
        ordernum: 99,
        creationtime: null,
        creatoruserid: 0,
        lastmodificationtime: null,
        lastmodifieruserid: 0,
        isdeleted: 0,
        deleteruserid: 0,
        deletiontime: null
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
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加频道";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getChannels(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改频道";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateChannels(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addChannels(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除频道编号为"' + ids + '"的数据项？').then(function() {
        return delChannels(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('cms/channels/export', {
        ...this.queryParams
      }, `channels_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>

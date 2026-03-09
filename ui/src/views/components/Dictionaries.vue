/* eslint-disable */
<script>
import {DICTIONARY_API} from "@/axios/axios";
import DictionaryTab from "@/views/components/DictionaryTab.vue";
import Navigator from "@/views/components/Navigator.vue";

export default {
  components: {DictionaryTab, Navigator},
  /* eslint-disable-next-line */
	name: 'Dictionaries',
	data: () => ({
    dictionaries: [],
    dictionaryTab: 0,
	}),
	methods: {

	},
	mounted() {
    DICTIONARY_API.get('getAll')
        .then(resp => {
          this.dictionaries = resp.data
        }).catch(e => {
      console.log(e)
    })
	},
}
</script>

<template>
  <v-container fluid>
    <Navigator/>
    <v-row>
      <v-col cols="1"/>
      <v-col cols="2">
        <v-toolbar flat>
          <v-toolbar-items>
            <v-tabs style="height: 1000px" v-model="dictionaryTab" vertical>
              <v-tabs-slider color="#283593"/>
              <v-tab v-for="dictionary in dictionaries" :key="dictionary.key" class="left-tab">
                {{dictionary.value }}
              </v-tab>
            </v-tabs>
          </v-toolbar-items>
        </v-toolbar>
      </v-col>
      <v-col cols="8">
        <v-tabs-items v-model="dictionaryTab">
          <v-tab-item v-for="dictionary in dictionaries" :key="dictionary.key">
            <DictionaryTab :dictionary="dictionary" :canEdit="dictionary.key !== 'LESSON_TIME' && dictionary.key !== 'SEMESTER'"/>
          </v-tab-item>
        </v-tabs-items>
      </v-col>
      <v-col cols="1"/>
    </v-row>
  </v-container>
</template>

<style lang="scss" scoped>
.left-tab {
  justify-content: left !important;
}
</style>